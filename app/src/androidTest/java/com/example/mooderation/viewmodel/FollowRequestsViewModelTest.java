package com.example.mooderation.viewmodel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.mooderation.FollowRequest;
import com.example.mooderation.backend.FollowRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FollowRequestsViewModelTest {
    @Mock
    FollowRepository followRepository;
    @Mock
    FollowRequest request;
    @Mock
    Observer<List<FollowRequest>> followRequestObserver;

    private FollowRequestsViewModel followRequestsViewModel;
    private MutableLiveData<List<FollowRequest>> followRequests = new MutableLiveData<>();

    // forces tasks to execute synchronously
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws ExecutionException, InterruptedException {
        MockitoAnnotations.initMocks(this);

        followRequestsViewModel = new FollowRequestsViewModel(followRepository);
        when(followRepository.getFollowRequests()).thenReturn(followRequests);

        followRequestsViewModel.getFollowRequests().observeForever(followRequestObserver);
    }

    @Test
    public void testAcceptFollowRequest() throws ExecutionException, InterruptedException {
        when(followRepository.accept(any())).then(invocation -> {
            followRequests.setValue(new ArrayList<>());
            return null;
        });

        followRequestsViewModel.acceptRequest(request);
        verify(followRequestObserver).onChanged(new ArrayList<>());
    }

    @Test
    public void testDenyFollowRequest() throws ExecutionException, InterruptedException {
        when(followRepository.deny(any())).then(invocation -> {
            followRequests.setValue(new ArrayList<>());
            return null;
        });

        followRequestsViewModel.denyRequest(request);
        verify(followRequestObserver).onChanged(new ArrayList<>());
    }
}
