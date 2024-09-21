# Features

    All the features of the application and behaviour should be defined
    here before developing it.

## Expense Listing and Searching

    For listing expeneses we are going to use a Filter object which
    contains the conditions that should be met by the returned list
    of expenses.

### Filter

1. isPersonal
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

#### isPersonal

    If it is true then all expenses which are all not part of a family
    only will be listed. If it is false then all the expenses of the
    user's family and the expenses which are all owned by the user will
    be taken.

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

    Note: If the "query" is not present then this criteria is not used.