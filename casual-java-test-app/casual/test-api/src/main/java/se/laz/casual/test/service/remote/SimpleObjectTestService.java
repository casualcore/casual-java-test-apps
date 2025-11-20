/*
 * Copyright (c) 2025, The casual project. All rights reserved.
 *
 * This software is licensed under the MIT license, https://opensource.org/licenses/MIT
 */

package se.laz.casual.test.service.remote;

import se.laz.casual.test.data.SimpleObject;

public interface SimpleObjectTestService
{
    /**
     * Echo the provided input marshalling to an from fieled.
     * @param echoMe
     * @return
     */
    SimpleObject echoFielded( SimpleObject echoMe );

}
