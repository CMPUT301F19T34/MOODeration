package com.example.mooderation.viewmodel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.mooderation.FollowRequest;
import com.example.mooderation.backend.FollowRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.verify;

public class FollowRequestsViewModelTest {
    @Mock
    FollowRepository followRepository;
    @Mock
    FollowRequest request;

    private FollowRequestsViewModel followRequestsViewModel;

    // forces tasks to execute synchronously
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws ExecutionException, InterruptedException {
        MockitoAnnotations.initMocks(this);
        followRequestsViewModel = new FollowRequestsViewModel(followRepository);
    }

    // TODO replace with better tests

    @Test
    public void testAcceptFollowRequest() throws ExecutionException, InterruptedException {
        followRequestsViewModel.acceptRequest(request);
        verify(followRepository).accept(request);
    }

    @Test
    public void testDenyFollowRequest() throws ExecutionException, InterruptedException {
        followRequestsViewModel.denyRequest(request);
        verify(followRepository).deny(request);
    }
}
