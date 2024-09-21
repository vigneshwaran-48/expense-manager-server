package com.vapps.expense.common.dto;

import lombok.Data;

@Data
public class StaticResourceDTO {

	public enum ContentType {
		IMAGE_JPG("image/jpg"),
		IMAGE_JPEG("image/jpeg"),
		IMAGE_PNG("image/png"),
		TEXT_HTML("text/html"),
		TEXT_PLAIN("text/plain"),
		APPLICATION_PDF("application/pdf");

		private String type;

		ContentType(String type) {
			this.type = type;
		}

		public String getType() {
			return type;
		}

		public static ContentType getContentType(String contentType) {
			for (ContentType cType : ContentType.values()) {
				if (cType.getType().equals(contentType)) {
					return cType;
				}
			}
			return null;
		}
	}

	public enum Visibility {
		PUBLIC,
		PRIVATE,
		FAMILY
	}

	private String id;
	private String ownerId;
	private byte[] data;
	private ContentType type;
	private Visibility visibility;
	private String name;

}
