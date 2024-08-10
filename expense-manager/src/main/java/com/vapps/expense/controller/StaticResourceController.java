package com.vapps.expense.controller;

import com.vapps.expense.common.dto.StaticResourceDTO;
import com.vapps.expense.common.dto.StaticResourceDTO.ContentType;
import com.vapps.expense.common.dto.response.Response;
import com.vapps.expense.common.dto.response.StaticResourceResponse;
import com.vapps.expense.common.exception.AppException;
import com.vapps.expense.common.service.StaticResourceService;
import com.vapps.expense.common.util.Endpoints;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping(Endpoints.STATIC_RESOURCE_API)
public class StaticResourceController {

    @Autowired
    private StaticResourceService staticResourceService;

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticResourceController.class);

    @PostMapping
    public ResponseEntity<StaticResourceResponse> addResource(@RequestParam("resource") MultipartFile resource,
            @RequestParam(name = "private", required = false, defaultValue = "false") boolean isPrivate,
            HttpServletRequest request, Principal principal) throws AppException {

        String userId = principal.getName();
        ContentType contentType = ContentType.getContentType(resource.getContentType());
        if (contentType == null) {
            throw new AppException(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                    "Unsupported Resource type " + resource.getContentType());
        }
        StaticResourceDTO staticResource = new StaticResourceDTO();
        try {
            staticResource.setType(contentType);
            staticResource.setName(resource.getName());
            staticResource.setData(resource.getBytes());
            staticResource.setVisibility(StaticResourceDTO.Visibility.PUBLIC);
            if (isPrivate) {
                staticResource.setVisibility(StaticResourceDTO.Visibility.PRIVATE);
            }
        } catch (Exception e) {
            LOGGER.error("Error while parsing the resource", e);
            throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error while parsing the resource");
        }
        staticResource = staticResourceService.addResource(userId, staticResource);
        return ResponseEntity.ok(new StaticResourceResponse(HttpStatus.OK.value(), "success", LocalDateTime.now(),
                request.getServletPath(), staticResource.getId()));
    }

    @GetMapping(Endpoints.GET_STATIC_RESOURCE_PATH)
    public ResponseEntity<?> getResource(@PathVariable("resourceId") String resourceId, HttpServletRequest request,
            Principal principal) throws AppException {

        String userId = principal.getName();
        Optional<StaticResourceDTO> resource = staticResourceService.getResource(userId, resourceId);
        if (resource.isEmpty()) {
            throw new AppException(HttpStatus.NOT_FOUND.value(), "The requested resource not found");
        }
        String contentType = resource.get().getType().getType();
        return ResponseEntity.ok().contentType(MediaType.valueOf(contentType)).body(resource.get().getData());
    }

    @DeleteMapping(Endpoints.DELETE_STATIC_RESOURCE_PATH)
    public ResponseEntity<Response> deleteResource(@PathVariable("resourceId") String resourceId,
            HttpServletRequest request, Principal principal) throws AppException {

        String userId = principal.getName();
        staticResourceService.deleteResource(userId, resourceId);
        return ResponseEntity.ok(new Response(HttpStatus.OK.value(), "Deleted resource!", LocalDateTime.now(),
                request.getServletPath()));
    }
}
