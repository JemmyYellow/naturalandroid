package com.jemmy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jemmy.common.Const;
import com.jemmy.utils.OkHttpCallback;
import com.jemmy.utils.OkHttpUtils;
import com.jemmy.utils.SharedPreferencesUtil;
import com.jemmy.viewmodel.UserViewModel;
import com.jemmy.vo.User;
import com.jemmy.vo.UserVO;

public class UserFragment extends Fragment implements View.OnClickListener {

    private UserViewModel mViewModel;
    private Button btn_signout;
    private TextView textView;
    private ImageView user_image;
    private AlertDialog.Builder builder;

    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        View view = inflater.inflate(R.layout.user_fragment, container, false);
        btn_signout = view.findViewById(R.id.signout);
        user_image = view.findViewById(R.id.userimage);
        textView = view.findViewById(R.id.et_username);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
        //将user放在viewmodel中
        SharedPreferencesUtil util = SharedPreferencesUtil.getInstance(requireActivity());
        UserVO userVO = new UserVO();
        User loginuser = (User) util.readObject("user", User.class);
        userVO.setUsername(loginuser.getUsername());
        userVO.setPhone(loginuser.getPhone());
        userVO.setRole(loginuser.getRole());
        mViewModel.setUser(loginuser);
        mViewModel.setUserVO(userVO);

        User user = (User) util.readObject("user", User.class);
        textView.setText(user.getUsername());
        textView.setOnClickListener(this);
        user_image.setOnClickListener(this);
        btn_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(getContext()).setMessage("注销用户").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferencesUtil util2 = SharedPreferencesUtil.getInstance(requireActivity());
                        util2.delete("isLogin");
                        util2.delete("user");
                        util2.clear();
                        OkHttpUtils.get(Const.IP_PORT+"/portal/user/signOut.do?username="+mViewModel.getUserVO().getUsername(),
                                new OkHttpCallback());
                        requireActivity().finish();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_username:
            case R.id.userimage:
                startActivity(new Intent(getActivity(), UserInfoActivity.class));
                break;
        }
    }
}
