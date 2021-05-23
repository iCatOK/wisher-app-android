package ru.omegapps.wisherapp.managers;

import android.content.Context;

import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import ru.omegapps.wisherapp.data_agents.FireBaseDataAgent;
import ru.omegapps.wisherapp.data_agents.LocalDataAgent;
import ru.omegapps.wisherapp.dto.Wish;
import ru.omegapps.wisherapp.dto.WishBlock;
import ru.omegapps.wisherapp.interfaces.DataAgent;

public class WishManager {

    public static String currentUid = "Test";
    public static ArrayList<String> sessionTags;
    public static String sessionAddresseeName = "";
    public static String sessionSex = "";
    private static ArrayList<WishBlock> sessionMainStack;
    private static ArrayList<WishBlock> sessionBeginStack;
    private static ArrayList<WishBlock> sessionMidStack;
    private static ArrayList<WishBlock> sessionEndStack;
    public static String sessionNameState = "";

    private final static DataAgent DATA_AGENT = new LocalDataAgent();
    private final static String NAME_PLACE_HOLDER = "{имя}";

    public static void resetSession(){
        sessionMainStack = new ArrayList<>();
        sessionBeginStack = new ArrayList<>();
        sessionMidStack = new ArrayList<>();
        sessionEndStack = new ArrayList<>();
        sessionTags = new ArrayList<>();
        sessionSex = "";
        sessionAddresseeName = "";
        sessionNameState = "";
    }

    public static ArrayList<WishBlock> getWishBlocksOfStep(String step){
        ArrayList<String> filters = new ArrayList<>(Collections.singletonList(step));
        if(!sessionNameState.isEmpty())
            filters.add(sessionNameState);
        if(!sessionSex.isEmpty())
            filters.add(sessionSex);
        return FireBaseDataAgent.getBlocksByTags(sessionTags, filters);
    }

    public static void pushToStepStack(WishBlock choosedWishBlock, String step){
        switch (step){
            case "begin":
                sessionBeginStack.add(choosedWishBlock);
                break;
            case "mid":
                sessionMidStack.add(choosedWishBlock);
                break;
            case "end":
                sessionEndStack.add(choosedWishBlock);
                break;
        }
    }

    public static String randomWishBlockPicker(){
        return null;
    }

    public static String generateCurrentWish(Context context){

        sessionMainStack = new ArrayList<>();
        sessionMainStack.addAll(sessionBeginStack);
        sessionMainStack.addAll(sessionMidStack);
        sessionMainStack.addAll(sessionEndStack);

        StringBuilder builder = new StringBuilder();
        for (WishBlock block : sessionMainStack) {
            String wish = sessionNameState.equals("") ?
                    block.getWishText().replace(NAME_PLACE_HOLDER, sessionAddresseeName) :
                    block.getWishText();
            builder.append(wish).append(" ");
        }

        Wish newWish = new Wish(
                sessionAddresseeName + ", " +
                        new SimpleDateFormat("dd/MM/yyyy")
                                .format(new Date()), builder.toString()
        );
        //DATA_AGENT.pushWish(newWish, currentUid);
        FireBaseDataAgent.pushWish(newWish, context);

        return builder.toString();
    }

    public static void resetStepStack(String stepName) {
        switch (stepName){
            case "begin":
                sessionBeginStack = new ArrayList<>();
                break;
            case "mid":
                sessionMidStack = new ArrayList<>();
                break;
            case "end":
                sessionEndStack = new ArrayList<>();
                break;
        }
    }
}
