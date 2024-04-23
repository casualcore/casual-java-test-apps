# casual java test apps

## casual-java-test-app

Basic test application
Note, needs jakarta

## Exported java services

### javaEcho

Echoes any payload

### javaForward

Makes a call to whatever the environment variable ```JAVA_FORWARD_SERVICE_NAME``` is pointing to

### commit

Works the same as javaEcho

### rollback

Returns a result with error state TPESVCFAIL

### work

Busy spinning for the time period, in milliseconds, that the environement variable ```CASUAL_JAVA_TEST_WORK_TIME``` is set to

### sleep

Sleeps for the time period, in milliseconds, that the environement variable ```CASUAL_JAVA_TEST_SLEEP_TIME``` is set to

### sink

Does nothing.

Note, this service does not return anything and should thus be called with ```TPNORETURN```

