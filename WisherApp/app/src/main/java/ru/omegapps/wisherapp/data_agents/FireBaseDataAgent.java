package ru.omegapps.wisherapp.data_agents;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import ru.omegapps.wisherapp.MainActivity;
import ru.omegapps.wisherapp.R;
import ru.omegapps.wisherapp.dto.Wish;
import ru.omegapps.wisherapp.dto.WishBlock;
import ru.omegapps.wisherapp.enums.WishBlockEnum;
import ru.omegapps.wisherapp.enums.WishEnum;
import ru.omegapps.wisherapp.fragments.HomeFragment;
import ru.omegapps.wisherapp.fragments.wishgen.WishGenSessionEndFragment;
import ru.omegapps.wisherapp.interfaces.DataAgent;

public class FireBaseDataAgent {

    private static FirebaseAuth mAuth;

    private static DatabaseReference wishRef;
    private static DatabaseReference wishBlockRef;
    private static DatabaseReference publicWishBlockRef;

    private static ArrayList<WishBlock> userWishBlocks;
    private static ArrayList<WishBlock> privateUserWishBlocks;
    private static ArrayList<WishBlock> publicWishBlocks;
    private static ArrayList<Wish> userWishes;

    static FirebaseUser currentUser;
    private static final String WISH_KEY = "Wishes";
    private static final String WBLOCK_KEY = "Wishblocks";
    private static final String PUBLIC_WBLOCK_KEY = "PublicWishblocks";

    public static void init(){
        wishRef = FirebaseDatabase.getInstance().getReference(WISH_KEY);
        wishBlockRef = FirebaseDatabase.getInstance().getReference(WBLOCK_KEY);
        publicWishBlockRef = FirebaseDatabase.getInstance().getReference(PUBLIC_WBLOCK_KEY);

        userWishBlocks = new ArrayList<>();
        userWishes = new ArrayList<>();
        publicWishBlocks = new ArrayList<>();
        privateUserWishBlocks = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        wishRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(currentUser == null)
                    return;
                else if (snapshot.child(currentUser.getUid()) == null)
                    return;
                userWishes.clear();
                DataSnapshot wishes = snapshot.child(currentUser.getUid());
                for(DataSnapshot ds: wishes.getChildren()) {
                    Wish newWish = ds.getValue(Wish.class);
                    if(newWish != null){
                        newWish.setUuid(ds.getKey());
                        userWishes.add(newWish);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("DEB", "Не удалось загрузить в статик блоке поздравления");
            }
        });

        wishBlockRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(currentUser == null)
                    return;
                else if (snapshot.child(currentUser.getUid()) == null)
                    return;
                userWishBlocks.clear();
                DataSnapshot wBlocks = snapshot.child(currentUser.getUid());
                for(DataSnapshot ds: wBlocks.getChildren()) {
                    WishBlock newWishBlock = ds.getValue(WishBlock.class);
                    if(newWishBlock != null){
                        if(newWishBlock.getTags() == null)
                            newWishBlock.setTags(new ArrayList<>());
                        userWishBlocks.add(newWishBlock);
                    }
                }
                Log.d("DEB", "Добавление прошло");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("DEB", "Не удалось загрузить в статик блоке поздравления");
            }
        });

        publicWishBlockRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                publicWishBlocks.clear();
                for(DataSnapshot ds: snapshot.getChildren()) {
                    WishBlock newWishBlock = ds.getValue(WishBlock.class);
                    if(newWishBlock != null){
                        if(currentUser != null){
                            if(newWishBlock.getUuid() != null && newWishBlock.getUuid().equals(currentUser.getUid()))
                                return;
                            if(newWishBlock.getTags() == null)
                                newWishBlock.setTags(new ArrayList<>());
                            if(newWishBlock.isPublic() && !newWishBlock.getUserUuid().equals(currentUser.getUid()))
                                publicWishBlocks.add(newWishBlock);
                        }
                    }
                }
                Log.d("DEB", "Добавление прошло");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("DEB", "Не удалось загрузить в статик блоке поздравления");
            }
        });

    }

    public static void logout() {
        if(mAuth == null)
            mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
    }

    public static ArrayList<WishBlock> getBlocksByTags(ArrayList<String> sessionTags, ArrayList<String> filters) {
        ArrayList<WishBlock> allBlocks = getAllNeededBlocks();
        return (ArrayList<WishBlock>) allBlocks.stream()
                .filter(block -> block.getFilters().containsAll(filters))
                .filter(block -> !(Collections.disjoint(block.getTags(), sessionTags) && !sessionTags.isEmpty()))
                .collect(Collectors.toList());
    }

    private static ArrayList<WishBlock> getAllNeededBlocks(){
        ArrayList<WishBlock> allBlocks = new ArrayList<>();
        allBlocks.addAll(userWishBlocks);
        allBlocks.addAll(publicWishBlocks);

        return allBlocks;
    }

    public static void pushWish(Wish newWish, Context context) {
        if(wishRef == null)
            wishRef = FirebaseDatabase.getInstance().getReference(WISH_KEY);

        if(currentUser == null){
            Toast.makeText(context, "Не авторизован, поэтому отправить нельзя!", Toast.LENGTH_SHORT).show();
            return;
        }
        wishRef.child(currentUser.getUid()).push().setValue(newWish).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                    Toast.makeText(context, "Успешно отправлено", Toast.LENGTH_SHORT).show();
                else{
                    Toast.makeText(context, "Не удалось отправить (проверьте интернет соединение)", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public static void updatePublicWishBlock(WishBlock newWishBlock) {
        if(newWishBlock.isPublic())
            publicWishBlockRef.child(newWishBlock.getUuid()).setValue(newWishBlock);
        else{
            deletePublicWishBlock(newWishBlock.getUuid());
        }
//            publicWishBlockRef.orderByChild("uuid").equalTo(newWishBlock.getUuid()).limitToFirst(1).getRef().removeValue();
    }

    public static void deletePublicWishBlock(String uuid){
        publicWishBlockRef.child(uuid).removeValue();
    }
}
