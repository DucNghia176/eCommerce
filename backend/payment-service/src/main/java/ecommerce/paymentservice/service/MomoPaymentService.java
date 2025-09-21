package ecommerce.paymentservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.apicommon1.model.response.ApiResponse;
import ecommerce.apicommon1.model.status.PaymentStatus;
import ecommerce.paymentservice.config.MomoConfig;
import ecommerce.paymentservice.dto.request.MomoRequest;
import ecommerce.paymentservice.dto.response.MomoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MomoPaymentService {
    private final MomoConfig momoConfig;

    public ApiResponse<MomoResponse> createPaymentUrl(MomoRequest request) {
        try {
            String requestId = UUID.randomUUID().toString();
            String orderInfo = "Thanh toán đơn hàng " + request.getOrderId();
            String orderIdStr = "ORDER_" + request.getOrderId();

            String amountStr = request.getAmount().stripTrailingZeros().toPlainString();

            String rawSignature =
                    "accessKey=" + momoConfig.getAccessKey()
                            + "&amount=" + amountStr
                            + "&extraData="
                            + "&ipnUrl=" + momoConfig.getNotifyUrl()
                            + "&orderId=" + orderIdStr
                            + "&orderInfo=" + orderInfo
                            + "&partnerCode=" + momoConfig.getPartnerCode()
                            + "&redirectUrl=" + momoConfig.getRedirectUrl()
                            + "&requestId=" + requestId
                            + "&requestType=captureWallet";

            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(momoConfig.getSecretKey().getBytes(), "HmacSHA256");
            hmac.init(secretKeySpec);
            String signature = Base64.getEncoder().encodeToString(hmac.doFinal(rawSignature.getBytes(StandardCharsets.UTF_8)));

            Map<String, String> payload = new HashMap<>();
            payload.put("accessKey", momoConfig.getAccessKey());
            payload.put("amount", amountStr);
            payload.put("extraData", "");
            payload.put("ipnUrl", momoConfig.getNotifyUrl());
            payload.put("orderId", orderIdStr);
            payload.put("orderInfo", orderInfo);
            payload.put("partnerCode", momoConfig.getPartnerCode());
            payload.put("redirectUrl", momoConfig.getRedirectUrl());
            payload.put("requestId", requestId);
            payload.put("requestType", "captureWallet");
            System.out.println("✅ rawSignature = [" + rawSignature + "]");
            System.out.println("✅ signature = [" + signature + "]");
            payload.put("signature", signature);
//            payload.put("lang", "vi");

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(payload);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(momoConfig.getEndpoint()))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            JsonNode responseJson = mapper.readTree(httpResponse.body());

            JsonNode payUrlNode = responseJson.get("payUrl");
            String payUrl = (payUrlNode != null) ? payUrlNode.asText() : null;

            if (payUrl == null) {
                return ApiResponse.<MomoResponse>builder()
                        .code(500)
                        .message("Không nhận được payUrl từ Momo. Phản hồi: " + responseJson.toString())
                        .data(null)
                        .build();
            }

            MomoResponse momoResponse = MomoResponse.builder()
                    .orderId(request.getOrderId())
                    .status(PaymentStatus.PENDING)
                    .payUrl(payUrl)
                    .build();

            return ApiResponse.<MomoResponse>builder()
                    .code(200)
                    .message("Tạo yêu cầu thanh toán thành công")
                    .data(momoResponse)
                    .build();

        } catch (Exception e) {
            return ApiResponse.<MomoResponse>builder()
                    .code(500)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }
}
