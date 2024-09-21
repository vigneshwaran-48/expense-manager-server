# Family Expense Manager

A expense manager app by which you can track your personal and family expenses.

### Features

1. Create or Join family to track expenses of your family.
2. Manager family member roles and their allowed actions.
3. Send invitation to people to join your family.
4. Create categories both for personal and your family to categorize your expense.
5. Create expense with invoices.

### TODO

1. List expenses
2. Search expenses with filter options.
3. Dashboard for personal and family.
4. Settings page for personal and family.
5. Notification listing. (Currently only notifying via email and not showing it in the app. No accept or reject interactive features!)

### Running


#### Building

Go into **expense-manager-common** and run the below command
    
```bash
  mvn clean package
```

Now need to install it to local repo,

```bash
  mvn install:install-file -Dfile=target/expense-manager-common-1.0-origin.jar -DgroupId=com.vapps.expense.common -DartifactId=expense-manager-common -Dversion=1.0 -Dpackaging=jar
```

Now go into **expense-manager** and run the below command to build with tests

```bash
  mvn clean package
```

#### Dependencies
Set the below environment variables

1. **FAMILY_EXPENSE_MANAGER_EMAIL_ID** (Email id used to send notification.)
2. **FAMILY_EXPENSE_MANAGER_APP_PASSWORD** (App password for the email id to send mails.)


Make sure you have mongodb installed on you machine.

#### Startup command

```bash
  java -jar target/expense-manager-0.0.1-SNAPSHOT.jar
```
