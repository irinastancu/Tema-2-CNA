package service;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import proto.UserGrpc;
import proto.UserOuterClass;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserImpl extends UserGrpc.UserImplBase {
    /*
     * We observe here that some words have an "@", this are Annotations. Annotations are used to provide supplement
     * information about a program. We can autogenerate this functions, in Intellij we can use the shortcut ctrl + O to
     * do this.
     * */
    Map<String, Pair<String, String>> zodiacSigns = new HashMap<String, Pair<String, String>>();

    String pathZodiac;

    public UserImpl() {
        //pathZodiac = getClass().getPackage().getResource("/zodiac.txt").toString();

        this.zodiacSigns = HashMapFromTextFile("C:/Users/Ina/Desktop/Tema2/Server/src/main/resources/zodiac.txt");
    }

    public static Map<String, Pair<String, String>> HashMapFromTextFile(String pathName) {
        Map<String, Pair<String, String>> map = new HashMap<>();
        BufferedReader br = null;

        try {
            // create file object
            File file = new File(pathName);
            FileReader fileReader = new FileReader(file);

            // create BufferedReader object from the File
            br = new BufferedReader(fileReader);

            String line = null;

            // read file line by line
            while ((line = br.readLine()) != null) {

                // split the line by :
                String[] parts = line.split(":");

                // first part is name, second is number
                String zodiacSign = parts[0].trim();
                String[] value = parts[1].split(",");

                String start = value[0].trim();
                String end = value[1].trim();

                MutablePair<String, String> period = new MutablePair<>(start, end);

                // put name, number in HashMap if they are
                // not empty
                if (!zodiacSign.equals("") && !period.equals(""))
                    map.put(zodiacSign, period);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            // Always close the BufferedReader
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                }
            }
        }

        return map;
    }


    boolean isWithinRange(Date birthDate, Date startDate, Date endDate) {
        boolean isBefore = birthDate.before(endDate);
        boolean isAfter = birthDate.after(startDate);
        boolean isZodiac = isBefore && isAfter;
        return isZodiac;
    }

    public String getZodiacSign(String birthdate) throws ParseException {

            for (Map.Entry<String, Pair<String, String>> entry : zodiacSigns.entrySet()) {

                DateFormat format = new SimpleDateFormat("MM/dd");
                Date userDate = null;
                Date zodiacBegin = null;
                Date zodiacEnd = null;

                userDate = format.parse(birthdate);
                zodiacBegin = format.parse(entry.getValue().getLeft());
                zodiacEnd = format.parse(entry.getValue().getRight());

                if (isWithinRange(userDate, zodiacBegin, zodiacEnd)) {
                    return entry.getKey();
                }

            }

        return "Capricorn";
    }

    @Override
    public void getInfo(UserOuterClass.InfoRequest request, StreamObserver<UserOuterClass.InfoReply> responseObserver) {

        String birthdate = request.getDate();

        if (!DateValidationClass.getInstance().isValid(birthdate)) {
            UserOuterClass.InfoReply reply = UserOuterClass.InfoReply.newBuilder().setMessage(request.getName() +
                    " your birthdate is invalid ").build();
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Birthdate is invalid").asRuntimeException());
        } else {

            String zodiacSign = null;
            try {
                zodiacSign = getZodiacSign(birthdate);
            } catch (ParseException e) {
                responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
            }

            UserOuterClass.InfoReply reply = UserOuterClass.InfoReply.newBuilder().setMessage("Hello " + request.getName()
                    + " your zodiac sign is " + zodiacSign + '\n').build();
            /* We can call multiple times onNext function if we have multiple replies, ex. in next commits */
            responseObserver.onNext(reply);

        }

        System.out.println("Information has been delivered");
        responseObserver.onCompleted();
    }


}
