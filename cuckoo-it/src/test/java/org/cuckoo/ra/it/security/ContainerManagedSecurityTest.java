/*
 * Copyright (C) 2012-2017 akquinet tech@spree GmbH
 *
 * This file is part of the Cuckoo Resource Adapter for SAP.
 *
 * Cuckoo Resource Adapter for SAP is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Cuckoo Resource Adapter for SAP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with Cuckoo Resource Adapter for SAP. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cuckoo.ra.it.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.ejb.EJB;
import javax.resource.ResourceException;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import org.cuckoo.ra.cci.ApplicationProperties;
import org.cuckoo.ra.cci.ApplicationPropertiesImpl;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.cuckoo.ra.it.util.ArquillianHelper.createEar;
import static org.cuckoo.ra.it.util.ArquillianHelper.createRar;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(Arquillian.class)
public class ContainerManagedSecurityTest {

    @EJB
    private SecurityTestEjb ejb;

    @Deployment(order = 1, testable = false)
    public static Archive createRarDeployment() {
        return createRar();
    }

    @Deployment(name = "ear", order = 2)
    public static EnterpriseArchive createDeployment() {
        final JavaArchive testJar = ShrinkWrap.create(JavaArchive.class, "rartest.jar")
                .addClasses(
                        SecurityTestEjb.class, SecurityTestEjbBean.class,
                        ContainerManagedSecurityTest.class
                );
        testJar.addAsManifestResource(
                ContainerManagedSecurityTest.class.getResource("/jboss5/cuckoo-jboss-beans.xml"),
                "cuckoo-jboss-beans.xml");

        return createEar(testJar, "/jboss5/cuckoo-secure-jboss-ds.xml");
    }

    @Test
    @OperateOnDeployment("ear")
    public void callsSapWithCredentialsOfLoggedInUser() throws Exception {
        LoginContext loginContext = login("SAPUSER2");
        String username = ejb.callFunctionAndReturnEisUser();
        loginContext.logout();

        assertThat(username, equalTo("SAPUSER2"));
    }

    @Test
    @OperateOnDeployment("ear")
    public void throwsSecurityExceptionWhenCallingSapWithWrongCredentials() throws LoginException, ResourceException {
        LoginContext loginContext = login("SAPUSER3");
        try {
            ejb.callFunctionAndReturnEisUser();
            fail("Expected to throw Exception");
        } catch (ResourceException e) {
            assertHasSecurityExceptionInStacktrace(e);
        } finally {
            loginContext.logout();
        }
    }

    @Test
    @OperateOnDeployment("ear")
    public void overwritesApplicationProvidedPropertiesWithSubjectProperties() throws Exception {
        ApplicationProperties properties = new ApplicationPropertiesImpl("NON_EXISTING_USER", "A_PASSWORD");
        LoginContext loginContext = login("SAPUSER2");
        String username = ejb.callFunctionWithCustomPropertiesAndReturnEisUser(properties);
        loginContext.logout();

        assertThat(username, equalTo("SAPUSER2"));
    }

    private void assertHasSecurityExceptionInStacktrace(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        String stacktrace = stringWriter.toString();
        assertTrue(stacktrace.contains(javax.resource.spi.SecurityException.class.getName()));
    }

    private LoginContext login(final String username) throws LoginException {
        LoginContext loginContext = new LoginContext("ejb-test", new Subject(),
                new TestCallbackHandler(username));
        loginContext.login();
        return loginContext;
    }

    private static class TestCallbackHandler implements CallbackHandler {

        private final String username;

        private TestCallbackHandler(String username) {
            this.username = username;
        }

        public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
            for (Callback callback : callbacks) {
                if (callback instanceof NameCallback) {
                    ((NameCallback) callback).setName(username);
                }
                if (callback instanceof PasswordCallback) {
                    ((PasswordCallback) callback).setPassword("password".toCharArray());
                }
            }
        }
    }
}
