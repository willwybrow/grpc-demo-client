package co.wdns.grpc_demo;

import co.wdns.grpc_demo.password_service.Password;
import co.wdns.grpc_demo.password_service.PasswordServiceGrpc;
import co.wdns.grpc_demo.password_service.PasswordSuggestionRequest;
import co.wdns.grpc_demo.user_service.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.IOException;
import java.util.UUID;

public class GrpcDemoClient {
    public static void main(String[] args) throws IOException, InterruptedException {

        UserServiceClient userServiceClient = new UserServiceClient();
        PasswordServiceClient passwordServiceClient = new PasswordServiceClient();

        String testUser = UUID.randomUUID().toString();
        String testPass = UUID.randomUUID().toString();

        System.out.println(testUser);

        UserRegistrationRequest userRegistrationRequest = UserRegistrationRequest.newBuilder().setUsername(testUser).setPassword(testPass).build();

        UserRegistrationResult userRegistrationResult = userServiceClient.getBlockingStub().registerUser(userRegistrationRequest);

        System.out.println(userRegistrationResult);

        LoginUserAttempt loginUserAttempt = LoginUserAttempt.newBuilder().setUsername(testUser).setPassword(testPass).setIpAddress("127.0.0.1").build();

        LoginUserResult loginUserResult = userServiceClient.getBlockingStub().loginUser(loginUserAttempt);

        System.out.println(loginUserResult);

        LogoutUserAttempt logoutUserAttempt = LogoutUserAttempt.newBuilder().setAccessTokenId(loginUserResult.getAccessToken().getId()).build();

        LogoutUserResult logoutUserResult = userServiceClient.getBlockingStub().logoutUser(logoutUserAttempt);

        System.out.println(logoutUserResult);

        passwordServiceClient.getBlockingStub().getPasswordSuggestions(PasswordSuggestionRequest.newBuilder().build()).forEachRemaining((System.out::println));

        System.out.println(passwordServiceClient.getBlockingStub().evaluatePasswordStrength(Password.newBuilder().setPassword("").build()));
        System.out.println(passwordServiceClient.getBlockingStub().evaluatePasswordStrength(Password.newBuilder().setPassword("abc123").build()));
        System.out.println(passwordServiceClient.getBlockingStub().evaluatePasswordStrength(Password.newBuilder().setPassword("password").build()));
        System.out.println(passwordServiceClient.getBlockingStub().evaluatePasswordStrength(Password.newBuilder().setPassword("Password1").build()));
        System.out.println(passwordServiceClient.getBlockingStub().evaluatePasswordStrength(Password.newBuilder().setPassword("P@55w0rd1!").build()));
        System.out.println(passwordServiceClient.getBlockingStub().evaluatePasswordStrength(Password.newBuilder().setPassword("2b920e54-cb61-4911-a42d-9dbe604a08a8").build()));
        System.out.println(passwordServiceClient.getBlockingStub().evaluatePasswordStrength(Password.newBuilder().setPassword("zootomy-zoosporangium-zounds-31f189108a2c-464e-zootoxin-2b920e54-cb61-4911-a42d-9dbe604a08a8").build()));

        System.out.println("Over.");
    }

    static class UserServiceClient {
        private final ManagedChannel channel;
        private final UserServiceGrpc.UserServiceBlockingStub blockingStub;
        private final UserServiceGrpc.UserServiceStub asyncStub;

        UserServiceClient() {
            this.channel = ManagedChannelBuilder.forAddress("localhost", 50050).usePlaintext().build();
            this.blockingStub = UserServiceGrpc.newBlockingStub(channel);
            this.asyncStub = UserServiceGrpc.newStub(channel);
        }

        public UserServiceGrpc.UserServiceBlockingStub getBlockingStub() {
            return blockingStub;
        }
    }

    static class PasswordServiceClient {
        private final ManagedChannel channel;
        private final PasswordServiceGrpc.PasswordServiceBlockingStub blockingStub;
        private final PasswordServiceGrpc.PasswordServiceStub asyncStub;

        PasswordServiceClient() {
            this.channel = ManagedChannelBuilder.forAddress("localhost", 50050).usePlaintext().build();
            this.blockingStub = PasswordServiceGrpc.newBlockingStub(channel);
            this.asyncStub = PasswordServiceGrpc.newStub(channel);
        }

        public PasswordServiceGrpc.PasswordServiceBlockingStub getBlockingStub() {
            return blockingStub;
        }
    }
}
