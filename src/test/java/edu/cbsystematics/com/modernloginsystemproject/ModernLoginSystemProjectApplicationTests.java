package edu.cbsystematics.com.modernloginsystemproject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class ModernLoginSystemProjectApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testPublicPage() throws Exception {
        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"));
            System.out.println("Test Public Page: Passed!");
        } catch (AssertionError e) {
            fail("Expected access to the public page.");
        }
    }

    @Test
    public void testUnauthorizedHomePageAccess() throws Exception {
        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/authorized"))
                    .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                    .andExpect(redirectedUrl("http://localhost/login"));
            System.out.println("Test Unauthorized Home Page Access: Passed!");
        } catch (AssertionError e) {
            fail("Expected unauthorized access to the home page.");
        }
    }

    @Test
    public void testAuthorizedPageAccessForStudent() throws Exception {
        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/authorized")
                            .with(SecurityMockMvcRequestPostProcessors.user("student@example.com").password("123")))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(view().name("logging/authorized"));
            System.out.println("Test Authorized Page Access for Student: Passed!");
        } catch (AssertionError e) {
            fail("Expected authorized access to the home page for student.");
        }
    }

    @Test
    public void testAuthorizedPageAccessForModerator() throws Exception {
        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/authorized")
                            .with(SecurityMockMvcRequestPostProcessors.user("moderator@example.com").password("123")))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(view().name("logging/authorized"));
            System.out.println("Test Authorized Page Access for Moderator: Passed!");
        } catch (AssertionError e) {
            fail("Expected authorized access to the home page for moderator.");
        }
    }

    @Test
    public void testAuthorizedPageAccessForAdmin() throws Exception {
        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/authorized")
                            .with(SecurityMockMvcRequestPostProcessors.user("admin@example.com").password("123")))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(view().name("logging/authorized"));
            System.out.println("Test Authorized Page Access for Admin: Passed!");
        } catch (AssertionError e) {
            fail("Expected authorized access to the home page for admin.");
        }
    }

    @Test
    public void testAccessToAdminPage() throws Exception {
        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/admin").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("http://localhost/login"));
            System.out.println("Test Access to Admin Page: Passed!");
        } catch (AssertionError e) {
            fail("Expected redirection to login page for unauthorized access to admin page.");
        }
    }

    @Test
    public void testAccessDeniedForStudentToAdminPage() throws Exception {
        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/admin/list").with(csrf())
                            .with(SecurityMockMvcRequestPostProcessors.user("student@example.com").password("123")))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/access-denied"));
            System.out.println("Test Access Denied for Student to Admin Page: Passed!");
        } catch (AssertionError e) {
            fail("Expected redirection to access-denied page for unauthorized access to admin list page.");
        }
    }

    @Test
    void loginStudentValidateLogout() throws Exception {
        try {
            // Perform login and validate authentication
            MvcResult mvcResult = mockMvc.perform(formLogin().user("student@example.com").password("123"))
                    .andExpect(authenticated())
                    .andReturn();

            // Get the session from the login result
            MockHttpSession httpSession = (MockHttpSession) mvcResult.getRequest().getSession(false);

            // Validate logout
            if (httpSession != null) {
                mockMvc.perform(MockMvcRequestBuilders.post("/logout").with(csrf()).session(httpSession))
                        .andExpect(unauthenticated())
                        .andExpect(redirectedUrl("/login?logout"));

                // Validate access to a secured resource after logout
                mockMvc.perform(MockMvcRequestBuilders.get("/student").session(httpSession))
                        .andExpect(unauthenticated())
                        .andExpect(status().is3xxRedirection());
            }
            System.out.println("Test Login, Validate, and Logout for Student: Passed!");
        } catch (AssertionError e) {
            fail("Expected successful login, authentication, and logout for student.");
        }
    }

    @Test
    public void testShowRegistrationForm() throws Exception {
        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/registration"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("logging/registration"));
            System.out.println("Test Show Registration Form: Passed!");
        } catch (AssertionError e) {
            fail("Expected registration form to be displayed.");
        }
    }

    @Test
    public void testSuccessfulRegistration() throws Exception {
        try {
            mockMvc.perform(MockMvcRequestBuilders.post("/registration")
                            .param("firstName", "Test")
                            .param("lastName", "Test")
                            .param("email", "test@example.com")
                            .param("confirmEmail", "test@example.com")
                            .param("password", "qweRTY0-=")
                            .param("confirmPassword", "qweRTY0-=")
                            .param("checkbox", "true")
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/registration?success"));
            System.out.println("Test Successful Registration: Passed!");
        } catch (AssertionError e) {
            fail("Expected Successful Registration.");
        }
    }


}
