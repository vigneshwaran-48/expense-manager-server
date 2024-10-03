# Features

    All the features of the application and behaviour should be defined
    here before developing it.

## Expense Listing and Searching: (Feature: 1)

    For listing expeneses we are going to use a Filter object which
    contains the conditions that should be met by the returned list
    of expenses.

### Filter

1. isFamily
2. start - end time
3. query
4. searchBy
   1. NAME
   2. DESCRIPTION
   3. CATEGORY
   4. OWNER
   5. ALL



    The filtering of the expenses will occur in the same order that has
    been listed above. Below will be the description for each criteria,

#### isFamily

    If it is true then only the expenses which are all owned by the
    user in his family and the expenses owned by its family will be 
    listed.
#### start - end time

    Expenses which are all have the time between these start and end
    time will be taken.

#### query

    If this query is present then a search based on the "searchBy" will
    be done on the expenses and the result will be returned.
    
    Note: This depends on the "searchBy" criteria.

#### searchBy

    The search action on expenses will be done with this field. If no
    value given for this the default value should be "ALL". 
    If the searchBy is "OWNER" then the respective family or user name
    will be matched with the search query.

    Note: If the "query" is not present then this criteria is not used.


## Expense Stats: (Feature: 1)

      Expenses can be maintained for both personal family expenses. We
      can distinguish between personal and family stats with a type enum
      and to find the user or family we will have a common "ownerId" to 
      store their ids.


      We will store all the stats in a single record that will contain
      this month, week total expenses amount. Recent expeneses, Top
      categories by amount, top user by expenses spent.