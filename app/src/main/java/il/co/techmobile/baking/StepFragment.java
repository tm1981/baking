package il.co.techmobile.baking;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

import il.co.techmobile.baking.modal.Step;


public class StepFragment extends Fragment {

    // Final Strings to store state information about the list of images and list index
    private static final String STEPS_LIST = "steps_list";
    private static final String LIST_INDEX = "list_index";

    private List<Step> steps;
    private int stepIndex = 0;
    private int numberOfSteps = 0;
    private TextView stepDescription;

    private PlayerView playerView;
    private SimpleExoPlayer simpleExoPlayer;

    private long playerPosition = 0;
    private boolean isPlaying;
    private boolean isFullScreen = true;
    private boolean isTablet = false;

    public StepFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(savedInstanceState != null) {
            stepIndex = savedInstanceState.getInt(LIST_INDEX);
            steps = savedInstanceState.getParcelableArrayList(STEPS_LIST);
            playerPosition = savedInstanceState.getLong("player_position");
            isPlaying = savedInstanceState.getBoolean("playing");
        }

        View rootView = inflater.inflate(R.layout.fragment_step,container,false);
        final LinearLayout linearLayout = rootView.findViewById(R.id.step_linear_layout);
        if (getContext() != null) {
            isTablet = getContext().getResources().getBoolean(R.bool.isTablet);
        }
        // gesture detector, detecting double tap to show action bar in full screen
        final GestureDetector gd = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                ToggleActionBar();
                return true;
            }
        });

        playerView = rootView.findViewById(R.id.player_view);
        //set touch listener
        playerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gd.onTouchEvent(event);
            }
        });

        stepDescription = rootView.findViewById(R.id.step_long_description);
        Button nextButton = rootView.findViewById(R.id.button_next);
        Button prevButton = rootView.findViewById(R.id.button_prev);

        if (isTablet) {
            nextButton.setVisibility(View.INVISIBLE);
            prevButton.setVisibility(View.INVISIBLE);
        } else {
            nextButton.setVisibility(View.VISIBLE);
            prevButton.setVisibility(View.VISIBLE);
        }


        if (getActivity() != null) {
            ViewTreeObserver viewTreeObserver = linearLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        linearLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        if (!isTablet) {
                            if (getActivity().getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                                int width = linearLayout.getWidth();
                                int height = (int) (0.562 * width);
                                playerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height));
                            } else {
                                DisplayMetrics displayMetrics = new DisplayMetrics();
                                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                                int displayHeight = displayMetrics.heightPixels;
                                playerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,displayHeight));
                            }
                        } else {
                            int width = linearLayout.getWidth();
                            int height = (int) (0.562 * width);
                            playerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height));
                        }



                    }
                });
            }
        }

        SetData(stepIndex);

        numberOfSteps = steps.size();
        VideoPlayer();

        nextButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (numberOfSteps > stepIndex + 1) {
                    stepIndex++;
                    ReleasePlayer();
                    VideoPlayer();
                    SetData(stepIndex);
                } else {
                    stepIndex = 0;
                    ReleasePlayer();
                    VideoPlayer();
                    SetData(stepIndex);
                }
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberOfSteps >= stepIndex + 1) {
                    if (stepIndex >= 1) {
                        stepIndex--;
                        ReleasePlayer();
                        VideoPlayer();
                        SetData(stepIndex);
                    } else {
                        stepIndex = numberOfSteps - 1;
                        ReleasePlayer();
                        VideoPlayer();
                        SetData(stepIndex);
                    }

                }
            }
        });

        return rootView;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public void setStepIndex(int stepIndex) {
        this.stepIndex = stepIndex;
    }


    private void SetData(int position) {
        stepDescription.setText(steps.get(position).getDescription());

        if (getActivity() != null) {
            ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            //set the action bar title only in phone where is only one panel
            if (actionBar != null && !isTablet) {
                actionBar.setTitle(steps.get(position).getShortDescription());
            }

        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(LIST_INDEX,stepIndex);
        outState.putParcelableArrayList(STEPS_LIST, (ArrayList<? extends Parcelable>) steps);
        if (simpleExoPlayer != null) {
            playerPosition = simpleExoPlayer.getContentPosition();
            outState.putLong("player_position",simpleExoPlayer.getContentPosition());
            outState.putBoolean("playing",simpleExoPlayer.getPlayWhenReady());
        }

    }


    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            VideoPlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || simpleExoPlayer == null)) {
            VideoPlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            ReleasePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            ReleasePlayer();
        }
    }

    private void VideoPlayer () {

        if (steps.get(stepIndex).getVideoURL().equals("")) {
            playerView.setVisibility(View.GONE);
        } else {
            if (getActivity() != null) {
                if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && !isTablet) {
                    View decorView = getActivity().getWindow().getDecorView();
                    // Hide the status bar.
                    int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                    decorView.setSystemUiVisibility(uiOptions);

                    android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
                    if (actionBar != null) {
                        actionBar.hide();
                    }
                }

            }

            // Measures bandwidth during playback. Can be null if not required.
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);

            // 2. Create the player
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);

            playerView.setPlayer(simpleExoPlayer);

            // Measures bandwidth during playback. Can be null if not required.
            DefaultBandwidthMeter bandwidthMeter2 = new DefaultBandwidthMeter();
            DataSource.Factory dataSourceFactory;
            if (getContext() != null) {
                // Produces DataSource instances through which media data is loaded.
                dataSourceFactory = new DefaultDataSourceFactory(getContext(),Util.getUserAgent(getContext(), "baking"),bandwidthMeter2);
                // This is the MediaSource representing the media to be played.
                MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(steps.get(stepIndex).getVideoURL()));
                // Prepare the player with the source.
                simpleExoPlayer.prepare(videoSource);
                simpleExoPlayer.setPlayWhenReady(false);
                if (playerPosition != 0) {
                    simpleExoPlayer.seekTo(playerPosition);
                    simpleExoPlayer.setPlayWhenReady(isPlaying);

                }
            }
            playerView.setVisibility(View.VISIBLE);
        }

    }

    private void ReleasePlayer() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
        }
    }


    private void ToggleActionBar () {

        if (getActivity() != null) {
            if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && !isTablet) {
                if (isFullScreen) {
                    android.support.v7.app.ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                    if (actionBar != null) {
                        actionBar.show();
                    }
                    isFullScreen = false;
                } else {
                    android.support.v7.app.ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                    if (actionBar != null) {
                        actionBar.hide();
                    }
                    isFullScreen = true;
                }
            }
        }
    }
}
