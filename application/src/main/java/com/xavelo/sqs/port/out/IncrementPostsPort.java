package com.xavelo.sqs.port.out;

import java.util.UUID;

public interface IncrementPostsPort {
    void incrementPosts(UUID id);
}
