/*
 * Copyright (c) 2024, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */
package se.laz.casual.test.service.remote;

import java.util.Optional;

public class EnvTime
{
    public static final String SLEEP_TIME_ENV_NAME = "CASUAL_JAVA_TEST_SLEEP_TIME";
    public static final String WORK_TIME_ENV_NAME = "CASUAL_JAVA_TEST_WORK_TIME";
    public static final String DEFAULT_TIME = "100";
    private EnvTime()
    {}
    public static EnvTime of()
    {
        return new EnvTime();
    }
    public long getWorkTime()
    {
        return getTimeInMillis(WORK_TIME_ENV_NAME);
    }
    public long getSleepTime()
    {
        return getTimeInMillis(SLEEP_TIME_ENV_NAME);
    }
    private long getTimeInMillis(String envName)
    {
        return Long.parseLong(Optional.ofNullable(System.getenv(envName)).orElse(DEFAULT_TIME));
    }
}
