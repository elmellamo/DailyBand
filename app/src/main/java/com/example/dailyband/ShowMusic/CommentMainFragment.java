package com.example.dailyband.ShowMusic;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyband.Love.LoveActivity;
import com.example.dailyband.Models.CommentItem;
import com.example.dailyband.Models.ComplexName;
import com.example.dailyband.Models.TestSong;
import com.example.dailyband.MusicAdd.AddMusic;
import com.example.dailyband.MusicFragment.NewPopularFragment;
import com.example.dailyband.R;
import com.example.dailyband.Utils.CommentDatailClickListener;
import com.example.dailyband.Utils.CommentMainCompletedListener;
import com.example.dailyband.Utils.FirebaseMethods;
import com.example.dailyband.Utils.OnCommentSuccessListener;
import com.example.dailyband.adapter.CommentMainAdapter;
import com.example.dailyband.adapter.LoveAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentMainFragment extends Fragment implements CommentDatailClickListener, CommentMainCompletedListener {

    private View view;
    private RecyclerView commentrecycler;
    private EditText comment_add_edit;
    private Button registerbtn;
    private ImageView clearimg;
    private String postId;
    private List<CommentItem> comments;
    private FirebaseMethods mFirebaseMethods;
    private CommentMainCompletedListener completedListener;
    private CommentMainAdapter adapter;
    private ConstraintLayout emptytxt;
    private NewPickMusic newPickMusic;
    public CommentMainFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public void setCommentMain(String postId){
        this.postId = postId;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.comment_main, container, false);

        commentrecycler = view.findViewById(R.id.commentrecycler);
        commentrecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        emptytxt = view.findViewById(R.id.emptytxt);
        comment_add_edit = view.findViewById(R.id.comment_add_edit);
        registerbtn = view.findViewById(R.id.registerbtn);
        clearimg = view.findViewById(R.id.clearimg);
        newPickMusic =(NewPickMusic) getActivity();

        mFirebaseMethods = new FirebaseMethods(getActivity());
        comments = new ArrayList<>();
        getComment();
        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String register_comment = comment_add_edit.getText().toString();

                if (!register_comment.isEmpty()) {
                    mFirebaseMethods.addToOriginalComment(register_comment, postId);
                    comment_add_edit.setText("");
                }
            }
        });

        return view;
    }

    private void getComment(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String myUID = mFirebaseMethods.getMyUid();
        DatabaseReference commentRef = databaseReference.child("comment").child(postId);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    comments.clear();
                    newPickMusic.showProgressBar();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CommentItem comment = snapshot.getValue(CommentItem.class);
                        if(comment != null){
                            comments.add(comment);
                        }
                    }
                    adapter = new CommentMainAdapter((Context) getActivity(), comments, CommentMainFragment.this, CommentMainFragment.this);
                    commentrecycler.setAdapter(adapter);

                    adapter.notifyDataSetChanged();
                    emptytxt.setVisibility(comments.size() == 0 ? View.VISIBLE : View.GONE);
                }else{
                    emptytxt.setVisibility(comments.size() == 0 ? View.VISIBLE : View.GONE);
                }
                //callback.onDataFetchedSuccessfully();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터를 가져오는데 실패한 경우 처리하는 코드
                //callback.onDataFetchFailed();
            }
        });
    }

    @Override
    public void onCommentDatailClicked(CommentItem commentItem) {
        if(getActivity() instanceof NewPickMusic){
            NewPickMusic newPickMusic = (NewPickMusic) getActivity();
            newPickMusic.changeDetail(commentItem);
        }
    }


    @Override
    public void onCommentMainCompleted() {
        if(getActivity() instanceof NewPickMusic){
            NewPickMusic newPickMusic = (NewPickMusic) getActivity();
            newPickMusic.hideProgressBar();
        }
    }
}