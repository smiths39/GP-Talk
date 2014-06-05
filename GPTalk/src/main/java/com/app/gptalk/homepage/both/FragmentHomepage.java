package com.app.gptalk.homepage.both;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.app.gptalk.R;

import java.lang.reflect.Field;

public class FragmentHomepage extends Fragment {

    private View view;
    private ImageView ivLogo;

    // Defining the complete rotation of the centralised animation
    private static final float ROTATE_FROM = 0.0f;
    private static final float ROTATE_TO = 360.0f;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getFragmentManager().popBackStack();

        if (view == null) {
            view = new View(getActivity());
        } else {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        view = inflater.inflate(R.layout.main_home_page, container, false);

        ivLogo = (ImageView) view.findViewById(R.id.ivGPTalkHome);

        // Launch the animation rotation
        RotateAnimation rotate = new RotateAnimation(ROTATE_FROM, ROTATE_TO, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration((long) 2 * 2000);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setRepeatMode(Animation.INFINITE);
        ivLogo.startAnimation(rotate);

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
