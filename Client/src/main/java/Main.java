import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import proto.UserGrpc;
import proto.UserOuterClass;

import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        /* *
         * Establish a connection to the server using the class ManagedChannelBuilder and the function usePlaintext().
         * usePlainText() should only be used for testing or for APIs where the use of such API or the data
         * exchanged is not sensitive.
         * */
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8999).usePlaintext().build();

        /* *
         * A blocking-style stub instance of Greeter service. We can have two types of stubs: blocking and async.
         * Blocking stubs are synchronous. Non-blocking stubs are asynchronous.
         * Take care if you want to call a rpc function on a blocking stub from UI thread
         * (cause an unresponsive/laggy UI).
         * */
        UserGrpc.UserBlockingStub bookStub = UserGrpc.newBlockingStub(channel);

        /* *
         * Asynchronous instance of the above declaration.
         * GreeterGrpc.GreeterStub bookStub = GreeterGrpc.newStub(channel);
         * */

        /* *
         * We can now use gRPC function via our instance of GreeterBlockingStub bookStub.
         * Below we call the function sayHello(Helloworld.HelloRequest request) with name field value set to "gRPC".
         * This function return a value of type  Helloworld.HelloReply that is saved in our instance reply.
         * We can get via generated functions every field from our message, in this case we have just one field.
         * */

        boolean isConnected = true;
        String name;
        String birthdate;

        while (isConnected) {
            System.out.println("Hello! You will have to insert your name and birthdate (MM/DD/YYYY format):\n");

            Scanner scan = new Scanner(System.in);

            System.out.println("Your name: ");
            name = scan.next();

            System.out.println("Your birthdate: ");
            birthdate = scan.next();

            if (DateValidationClass.getInstance().isValid(birthdate)) {
                UserOuterClass.InfoReply reply = bookStub.getInfo(UserOuterClass.InfoRequest.newBuilder().setName(name).
                        setDate(birthdate).build());
                System.out.println(reply.getMessage());
            } else {
                System.out.println("The date you entered is invalid.");
            }
        }

        channel.shutdown();
    }
}
