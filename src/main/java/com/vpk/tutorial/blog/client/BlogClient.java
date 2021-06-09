package com.vpk.tutorial.blog.client;

import com.proto.blog.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class BlogClient {
    public static void main(String[] args) {
        System.out.println("Hello, I'm gRPC Blog Client");
        BlogClient client = new BlogClient();
        client.run();
    }

    private void run(){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",50051)
                .usePlaintext()
                .build();

        create(channel);
        read(channel);
        update(channel);
    }

    private void create(ManagedChannel channel){
        BlogServiceGrpc.BlogServiceBlockingStub blogClient = BlogServiceGrpc.newBlockingStub(channel);

        CreateBlogResponse createBlogResponse = blogClient.createBlog(CreateBlogRequest.newBuilder()
                .setBlog(Blog.newBuilder()
                        .setAuthorId("103")
                        .setTitle("Computer Network 2")
                        .setContent("CSE")
                        .build())
                .build());

        System.out.println("Received create blog response: \n"+createBlogResponse.toString());
    }

    private void read(ManagedChannel channel){
        BlogServiceGrpc.BlogServiceBlockingStub blogClient = BlogServiceGrpc.newBlockingStub(channel);
        ReadBlogResponse readBlogResponse = blogClient.readBlog(
                ReadBlogRequest.newBuilder()
                        .setBlogId("5feec273a304ac79b3d8054a")
                        .build());
        System.out.println("Received read blog response: \n"+readBlogResponse.toString());
    }

    private void update(ManagedChannel channel){
        BlogServiceGrpc.BlogServiceBlockingStub blogClient = BlogServiceGrpc.newBlockingStub(channel);
        UpdateBlogResponse response = blogClient.updateBlog(
                UpdateBlogRequest.newBuilder()
                        .setBlog(Blog.newBuilder()
                                .setAuthorId("New Author 2")
                                .setContent("New Content 3")
                                .setTitle("New Title - Book 3")
                                .setId("5fef049bbfea7606bea44ef4")
                                .build())
                        .build()
        );
        System.out.println("Received update blog response: \n"+response.toString());
    }
}
