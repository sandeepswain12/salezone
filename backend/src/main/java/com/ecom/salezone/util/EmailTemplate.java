package com.ecom.salezone.util;

import com.ecom.salezone.dtos.OrderDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailTemplate {

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
}