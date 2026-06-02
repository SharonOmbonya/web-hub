package com.web_hub.web_hub.emailService;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;


    // ------------------------------------
// SET PASSWORD EMAIL (AFTER APPROVAL)
// ------------------------------------
    @Async
    public void sendSetPasswordEmail(String to, String setPasswordUrl) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Set Your Password – Account Approved");
            message.setText(
                    "Hello 👋\n\n" +
                            "Good news! Your account has been APPROVED.\n\n" +
                            "To activate your account, please set your password using the link below:\n\n" +
                            "👉 " + setPasswordUrl + "\n\n" +
                            "⏰ This link expires in 24 hours.\n\n" +
                            "If you did not request this, please ignore this email.\n\n" +
                            "Regards,\nMeraki Systems Limited"
            );

            mailSender.send(message);
            logger.info("Set password email sent to {}", to);

        } catch (Exception e) {
            logger.error("Failed to send set password email to {}: {}", to, e.getMessage());
        }
    }

    // ------------------------------------
// SEND MFA OTP (SUPER ADMIN)
// ------------------------------------
    @Async
    public void sendMfaOtp(String to, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Security Verification Code (MFA)");
            message.setText(
                    "⚠️ Security Verification Required\n\n" +
                            "Your one-time MFA code is:\n\n" +
                            otp + "\n\n" +
                            "This code will expire in 5 minutes.\n\n" +
                            "If this was not you, please secure your account immediately."
            );

            mailSender.send(message);
            logger.info("MFA OTP sent to {}", to);

        } catch (Exception e) {
            logger.error("Failed to send MFA OTP to {}: {}", to, e.getMessage());
        }
    }


    // ------------------------------------
    // SEND EMAIL VERIFICATION OTP
    // ------------------------------------
    @Async
    public void sendEmailVerificationOtp(String to, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Verify Your Email - Meraki Web Portal");
            message.setText(
                    "Welcome to Meraki Web Portal 👋\n\n" +
                            "Your email verification code is:\n\n" +
                            otp + "\n\n" +
                            "This code will expire in 10 minutes.\n\n" +
                            "If you did not request this, please ignore this email."
            );

            mailSender.send(message);
            logger.info("Email verification OTP sent to {}", to);

        } catch (Exception e) {
            logger.error("Failed to send email verification OTP to {}: {}", to, e.getMessage());
        }
    }

    // ------------------------------------
    // TENANT STATUS NOTIFICATIONS
    // ------------------------------------
    @Async
    public void sendTenantStatusEmail(String to, String status) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Tenant Account Status Update");
            message.setText(
                    "Hello,\n\n" +
                            "Your tenant account status has been updated to: " + status + ".\n\n" +
                            "If you have any questions, please contact management.\n\n" +
                            "Regards,\nMeraki Web Portal Team"
            );

            mailSender.send(message);
            logger.info("Tenant status email sent to {}", to);

        } catch (Exception e) {
            logger.error("Failed to send tenant status email to {}: {}", to, e.getMessage());
        }
    }

    // ------------------------------------
    // PASSWORD RESET CONFIRMATION
    // ------------------------------------
    @Async
    public void sendPasswordResetConfirmation(String to) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Password Reset Successful");
            message.setText(
                    "Your password has been reset successfully.\n\n" +
                            "If this was not you, please contact support immediately."
            );

            mailSender.send(message);
            logger.info("Password reset confirmation email sent to {}", to);

        } catch (Exception e) {
            logger.error("Failed to send password reset confirmation email to {}: {}", to, e.getMessage());
        }
    }


    @Async
    public void sendPasswordResetOtp(String to, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Password Reset Verification Code");
            message.setText(
                    "You requested a password reset.\n\n" +
                            "Your verification code is: " + otp + "\n\n" +
                            "This code expires in 15 minutes.\n\n" +
                            "If you did not request this, please secure your account immediately."
            );
            mailSender.send(message);
            logger.info("Password reset OTP sent to {}", to);
        } catch (Exception e) {
            logger.error("Failed to send password reset OTP to {}: {}", to, e.getMessage());
        }
    }


    // ------------------------------------
// EMPLOYEE APPROVAL EMAIL
// ------------------------------------
    @Async
    public void sendEmployeeApprovedEmail(String to, String landlordName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Employee Account Approved");
            message.setText(
                    "Hello,\n\n" +
                            "Good news 🎉\n\n" +
                            "Your employee account has been APPROVED by " + landlordName + ".\n\n" +
                            "You can now log in and start managing assigned properties.\n\n" +
                            "Regards,\nMeraki Web Portal Team"
            );

            mailSender.send(message);
            logger.info("Employee approval email sent to {}", to);

        } catch (Exception e) {
            logger.error("Failed to send employee approval email to {}: {}", to, e.getMessage());
        }
    }

    // ------------------------------------
// EMPLOYEE REJECTION EMAIL
// ------------------------------------
    @Async
    public void sendEmployeeRejectedEmail(String to, String landlordName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Employee Application Update");
            message.setText(
                    "Hello,\n\n" +
                            "We regret to inform you that your employee application was NOT approved by "
                            + landlordName + ".\n\n" +
                            "If you believe this was a mistake, please contact management.\n\n" +
                            "Regards,\nMeraki Web Portal Team"
            );

            mailSender.send(message);
            logger.info("Employee rejection email sent to {}", to);

        } catch (Exception e) {
            logger.error("Failed to send employee rejection email to {}: {}", to, e.getMessage());
        }
    }


    // ------------------------------------
// EMPLOYEE PROPERTY ASSIGNMENT EMAIL
// ------------------------------------
    @Async
    public void sendEmployeeAssignedToPropertyEmail(
            String to,
            String propertyName
    ) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Property Assignment Notification");
            message.setText(
                    "Hello,\n\n" +
                            "You have been assigned to manage the following property:\n\n" +
                            "🏢 Property: " + propertyName + "\n\n" +
                            "Please log in to the system to view your responsibilities.\n\n" +
                            "Regards,\nMeraki Web Portal Team"
            );

            mailSender.send(message);
            logger.info("Employee property assignment email sent to {}", to);

        } catch (Exception e) {
            logger.error("Failed to send property assignment email to {}: {}", to, e.getMessage());
        }
    }

    // ------------------------------------
// EMPLOYEE WELCOME EMAIL (FIRST LOGIN)
// ------------------------------------
    @Async
    public void sendEmployeeWelcomeEmail(
            String to,
            String temporaryPassword,
            String loginUrl
    ) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Welcome to Meraki Web Portal");
            message.setText(
                    "Welcome 👋\n\n" +
                            "Your employee account has been created successfully.\n\n" +
                            "🔐 Login Details:\n" +
                            "Email: " + to + "\n" +
                            "Temporary Password: " + temporaryPassword + "\n\n" +
                            "👉 Login here: " + loginUrl + "\n\n" +
                            "⚠️ IMPORTANT:\n" +
                            "You will be REQUIRED to change your password after logging in.\n\n" +
                            "If you did not expect this email, please contact management.\n\n" +
                            "Regards,\nMeraki Web Portal Team"
            );

            mailSender.send(message);
            logger.info("Employee welcome email sent to {}", to);

        } catch (Exception e) {
            logger.error("Failed to send employee welcome email to {}: {}", to, e.getMessage());
        }
    }


    // ------------------------------------
// ONBOARDING INVITATION EMAIL
// ------------------------------------
    @Async
    public void sendOnboardingInvite(String to, String onboardingUrl) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("You're Invited – Complete Your Onboarding");
            message.setText(
                    "Hello 👋\n\n" +
                            "You have been invited to join Meraki Web Portal.\n\n" +
                            "👉 Complete your onboarding here:\n" +
                            onboardingUrl + "\n\n" +
                            "⏰ This link expires in 48 hours.\n\n" +
                            "If you did not expect this invitation, please ignore this email.\n\n" +
                            "Regards,\nMeraki Web Portal Team"
            );

            mailSender.send(message);
            logger.info("Onboarding invite email sent to {}", to);

        } catch (Exception e) {
            logger.error("Failed to send onboarding invite email to {}: {}", to, e.getMessage());
        }
    }

    @Async
    public void sendAccountApprovedEmail(String to, String role) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Account Approved");
        message.setText(
                "Hello,\n\n" +
                        "Your account has been APPROVED.\n\n" +
                        "Role: " + role + "\n\n" +
                        "You can now set your password and log in.\n\n" +
                        "Regards,\nMeraki Web Portal Team"
        );
        mailSender.send(message);
    }

    @Async
    public void sendAccountRejectedEmail(String to, String role) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Account Rejected");
        message.setText(
                "Hello,\n\n" +
                        "Your account request for role " + role + " was rejected.\n\n" +
                        "If you believe this is a mistake, contact support.\n\n" +
                        "Regards,\nMeraki Web Portal Team"
        );
        mailSender.send(message);
    }

    @Async
    public void sendLoginOtp(String to, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Login Verification Code");
            message.setText(
                    "Your login verification code is:\n\n" +
                            otp + "\n\n" +
                            "This code expires in 5 minutes.\n\n" +
                            "If this was not you, please secure your account."
            );

            mailSender.send(message);
            logger.info("Login OTP sent to {}", to);

        } catch (Exception e) {
            logger.error("Failed to send login OTP to {}: {}", to, e.getMessage());
        }
    }


}
