package com.ecom.salezone.util;

import com.ecom.salezone.dtos.OrderDto;
import com.ecom.salezone.enums.OtpType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailTemplates {

    @Value("${app.cors.front-end-url}")
    private String frontendUrl;

    public String getOrderSuccessTemplate(OrderDto order) {

        StringBuilder itemsHtml = new StringBuilder();

        order.getOrderItems().forEach(item -> {
            itemsHtml.append("""
                <tr>
                    <td style="padding:10px;">%s</td>
                    <td style="padding:10px; text-align:center;">%d</td>
                    <td style="padding:10px; text-align:right;">₹%.2f</td>
                </tr>
            """.formatted(
                    item.getProduct().getTitle(),
                    item.getQuantity(),
                    (double) item.getTotalPrice()
            ));
        });

        return """
        <div style="background-color:#f5f5f5; padding:20px; font-family:Arial, sans-serif;">
            
            <div style="max-width:600px; margin:auto; background:white; border-radius:10px; overflow:hidden; box-shadow:0 2px 8px rgba(0,0,0,0.1);">

                <!-- HEADER -->
                <div style="background:#4CAF50; color:white; padding:15px; text-align:center;">
                    <h2 style="margin:0;">SaleZone 🛒</h2>
                </div>

                <!-- BODY -->
                <div style="padding:20px;">

                    <h3 style="color:#4CAF50;">Order Confirmed ✅</h3>

                    <p>Hi <b>%s</b>,</p>

                    <p>Your order <b>#%s</b> has been successfully placed 🎉</p>

                    <!-- TABLE -->
                    <table style="width:100%%; border-collapse:collapse; margin-top:15px;">
                        <tr style="background:#f2f2f2;">
                            <th style="padding:10px; text-align:left;">Product</th>
                            <th style="padding:10px; text-align:center;">Qty</th>
                            <th style="padding:10px; text-align:right;">Price</th>
                        </tr>
                        %s
                    </table>

                    <!-- TOTAL -->
                    <div style="text-align:right; margin-top:15px;">
                        <h3>Total: ₹%.2f</h3>
                    </div>

                    <!-- BUTTON -->
                    <div style="text-align:center; margin-top:20px;">
                        <a href="%s/orders" 
                           style="background:#4CAF50; color:white; padding:12px 20px; text-decoration:none; border-radius:5px;">
                           View Order
                        </a>
                    </div>

                    <p style="margin-top:20px;">We’ll notify you once your order is shipped 🚚</p>

                </div>

                <!-- FOOTER -->
                <div style="background:#f9f9f9; padding:15px; text-align:center; font-size:12px; color:#777;">
                    <p>© 2026 SaleZone. All rights reserved.</p>
                    <p>If you did not place this order, please contact support.</p>
                </div>

            </div>
        </div>
        """.formatted(
                order.getUser().getUserName(),
                order.getOrderId(),
                itemsHtml.toString(),
                (double) order.getOrderAmount(),
                frontendUrl
        );
    }

    public String getOtpTemplate(String userName, String otp, OtpType type) {

        String title = (type == OtpType.REGISTRATION)
                ? "Verify Your SaleZone Account"
                : "Your SaleZone Login OTP";

        String subtitle = (type == OtpType.REGISTRATION)
                ? "Use the code below to verify your email and activate your account."
                : "Use the code below to complete your login. Do not share it with anyone.";

        return """
        <div style="background-color:#f5f5f5; padding:20px; font-family:Arial, sans-serif;">
            <div style="max-width:600px; margin:auto; background:white; border-radius:10px; overflow:hidden; box-shadow:0 2px 8px rgba(0,0,0,0.1);">

                <div style="background:#4CAF50; color:white; padding:15px; text-align:center;">
                    <h2 style="margin:0;">SaleZone 🛒</h2>
                </div>

                <div style="padding:30px;">
                    <h3 style="color:#4CAF50;">%s</h3>
                    <p>Hi <b>%s</b>,</p>
                    <p>%s</p>

                    <div style="text-align:center; margin:30px 0;">
                        <div style="display:inline-block; background:#f2f2f2; border-radius:10px; padding:20px 40px;">
                            <h1 style="margin:0; letter-spacing:10px; color:#333; font-size:36px;">%s</h1>
                        </div>
                    </div>

                    <p style="color:#e53935; font-weight:bold; text-align:center;">
                        This code expires in 5 minutes.
                    </p>

                    <p style="color:#777; font-size:13px;">
                        If you did not request this, please ignore this email.
                    </p>
                </div>

                <div style="background:#f9f9f9; padding:15px; text-align:center; font-size:12px; color:#777;">
                    <p>© 2026 SaleZone. All rights reserved.</p>
                </div>
            </div>
        </div>
    """.formatted(title, userName, subtitle, otp);
    }

    public String getWelcomeBackTemplate(String userName) {

        return """
        <div style="background-color:#f5f5f5; padding:20px; font-family:Arial, sans-serif;">
            <div style="max-width:600px; margin:auto; background:white; border-radius:10px; overflow:hidden; box-shadow:0 2px 8px rgba(0,0,0,0.1);">

                <!-- HEADER -->
                <div style="background:#4CAF50; color:white; padding:15px; text-align:center;">
                    <h2 style="margin:0;">SaleZone 🛒</h2>
                </div>

                <!-- BODY -->
                <div style="padding:30px;">

                    <h3 style="color:#4CAF50;">Welcome back, %s! 👋</h3>

                    <p>You have successfully logged in to your SaleZone account.</p>

                    <p>If this was not you, please
                        <a href="%s/auth/reset-password" style="color:#e53935;">
                            secure your account immediately
                        </a>.
                    </p>

                    <div style="background:#f9f9f9; border-left:4px solid #4CAF50; padding:15px; margin:20px 0; border-radius:0 8px 8px 0;">
                        <p style="margin:0; font-size:13px; color:#555;">
                            Time: %s<br/>
                            If you did not perform this login, reset your password right away.
                        </p>
                    </div>

                    <div style="text-align:center; margin-top:20px;">
                        <a href="%s"
                           style="background:#4CAF50; color:white; padding:12px 24px; text-decoration:none; border-radius:5px;">
                           Go to SaleZone
                        </a>
                    </div>

                </div>

                <!-- FOOTER -->
                <div style="background:#f9f9f9; padding:15px; text-align:center; font-size:12px; color:#777;">
                    <p>© 2026 SaleZone. All rights reserved.</p>
                </div>

            </div>
        </div>
    """.formatted(
                userName,
                frontendUrl,
                java.time.format.DateTimeFormatter
                        .ofPattern("dd MMM yyyy, hh:mm a")
                        .format(java.time.LocalDateTime.now()),
                frontendUrl
        );
    }

    public String getPasswordResetOtpTemplate(String userName, String otp) {

        return """
        <div style="background-color:#f5f5f5; padding:20px; font-family:Arial, sans-serif;">
            <div style="max-width:600px; margin:auto; background:white; border-radius:10px; overflow:hidden; box-shadow:0 2px 8px rgba(0,0,0,0.1);">

                <div style="background:#e53935; color:white; padding:15px; text-align:center;">
                    <h2 style="margin:0;">SaleZone 🛒</h2>
                </div>

                <div style="padding:30px;">
                    <h3 style="color:#e53935;">Password Reset Request 🔐</h3>
                    <p>Hi <b>%s</b>,</p>
                    <p>We received a request to reset your password. Use the OTP below:</p>

                    <div style="text-align:center; margin:30px 0;">
                        <div style="display:inline-block; background:#f2f2f2; border-radius:10px; padding:20px 40px;">
                            <h1 style="margin:0; letter-spacing:10px; color:#333; font-size:36px;">%s</h1>
                        </div>
                    </div>

                    <p style="color:#e53935; font-weight:bold; text-align:center;">
                        This code expires in 5 minutes.
                    </p>
                    <p style="color:#777; font-size:13px;">
                        If you did not request a password reset, please ignore this email.
                        Your password will not be changed.
                    </p>
                </div>

                <div style="background:#f9f9f9; padding:15px; text-align:center; font-size:12px; color:#777;">
                    <p>© 2026 SaleZone. All rights reserved.</p>
                </div>
            </div>
        </div>
    """.formatted(userName, otp);
    }

    public String getPasswordResetLinkTemplate(String userName, String resetLink) {

        return """
        <div style="background-color:#f5f5f5; padding:20px; font-family:Arial, sans-serif;">
            <div style="max-width:600px; margin:auto; background:white; border-radius:10px; overflow:hidden; box-shadow:0 2px 8px rgba(0,0,0,0.1);">

                <div style="background:#e53935; color:white; padding:15px; text-align:center;">
                    <h2 style="margin:0;">SaleZone 🛒</h2>
                </div>

                <div style="padding:30px;">
                    <h3 style="color:#e53935;">Password Reset Link 🔗</h3>
                    <p>Hi <b>%s</b>,</p>
                    <p>Click the button below to reset your password. This link is valid for <b>15 minutes</b>.</p>

                    <div style="text-align:center; margin:30px 0;">
                        <a href="%s"
                           style="background:#e53935; color:white; padding:14px 28px; text-decoration:none; border-radius:5px; font-size:15px;">
                           Reset My Password
                        </a>
                    </div>

                    <p style="color:#777; font-size:13px; text-align:center;">
                        Or copy this link: <a href="%s" style="color:#e53935;">%s</a>
                    </p>

                    <p style="color:#777; font-size:13px;">
                        If you did not request a password reset, please ignore this email.
                        Your password will not be changed.
                    </p>
                </div>

                <div style="background:#f9f9f9; padding:15px; text-align:center; font-size:12px; color:#777;">
                    <p>© 2026 SaleZone. All rights reserved.</p>
                </div>
            </div>
        </div>
    """.formatted(userName, resetLink, resetLink, resetLink);
    }

    public String getPasswordChangedTemplate(String userName) {

        return """
        <div style="background-color:#f5f5f5; padding:20px; font-family:Arial, sans-serif;">
            <div style="max-width:600px; margin:auto; background:white; border-radius:10px; overflow:hidden; box-shadow:0 2px 8px rgba(0,0,0,0.1);">

                <div style="background:#4CAF50; color:white; padding:15px; text-align:center;">
                    <h2 style="margin:0;">SaleZone 🛒</h2>
                </div>

                <div style="padding:30px;">
                    <h3 style="color:#4CAF50;">Password Changed Successfully ✅</h3>
                    <p>Hi <b>%s</b>,</p>
                    <p>Your SaleZone account password was changed successfully.</p>

                    <div style="background:#fff3e0; border-left:4px solid #e53935; padding:15px; margin:20px 0; border-radius:0 8px 8px 0;">
                        <p style="margin:0; font-size:13px; color:#555;">
                            Time: %s<br/>
                            If you did not make this change, please
                            <a href="%s/support" style="color:#e53935;">contact support immediately</a>.
                        </p>
                    </div>
                </div>

                <div style="background:#f9f9f9; padding:15px; text-align:center; font-size:12px; color:#777;">
                    <p>© 2026 SaleZone. All rights reserved.</p>
                </div>
            </div>
        </div>
    """.formatted(
                userName,
                java.time.format.DateTimeFormatter
                        .ofPattern("dd MMM yyyy, hh:mm a")
                        .format(java.time.LocalDateTime.now()),
                frontendUrl
        );
    }
}