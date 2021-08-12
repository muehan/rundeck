/*
 * Copyright 2021 Rundeck, Inc. (http://rundeck.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dtolabs.rundeck.core.execution.impl.jsch;

import com.dtolabs.rundeck.core.common.INodeEntry;
import com.dtolabs.rundeck.core.execution.ExecutionContext;
import com.dtolabs.rundeck.core.execution.proxy.DefaultSecretBundle;
import com.dtolabs.rundeck.core.execution.proxy.SecretBundle;
import com.dtolabs.utils.Streams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class JschSecretBundleUtil {

    public static SecretBundle createBundle(final ExecutionContext context, final INodeEntry node)  {
        try {
            DefaultSecretBundle secretBundle = new DefaultSecretBundle();
            final NodeSSHConnectionInfo nodeAuthentication = new NodeSSHConnectionInfo(node, context.getFramework(),
                                                                                       context
            );
            if(nodeAuthentication.getPasswordStoragePath() != null) {
                secretBundle.addSecret(
                        nodeAuthentication.getPasswordStoragePath(),
                        nodeAuthentication.getPasswordStorageData()
                );
            }
            if(nodeAuthentication.getPrivateKeyPassphraseStoragePath() != null) {
                secretBundle.addSecret(
                        nodeAuthentication.getPrivateKeyPassphraseStoragePath(),
                        nodeAuthentication.getPrivateKeyPassphraseStorageData()
                );
            }
            if(nodeAuthentication.getPrivateKeyStoragePath() != null) {
                ByteArrayOutputStream pkData = new ByteArrayOutputStream();
                Streams.copyStream(nodeAuthentication.getPrivateKeyStorageData(), pkData);
                secretBundle.addSecret(
                        nodeAuthentication.getPrivateKeyStoragePath(),
                        pkData.toByteArray()
                );
            }
            return secretBundle;
        } catch(IOException iex) {
            throw new RuntimeException("Unable to prepare secret bundle", iex);
        }
    }
}
