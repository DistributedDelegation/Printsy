package com.mvp.resolver;

import org.springframework.stereotype.Component;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import graphql.schema.DataFetcher;

import com.mvp.model.Image;

@Controller
public class Mutation {

    @MutationMapping
    public String submitImageId(@Argument String id) {
        return "Image ID submitted: " + id + "\nLink: " + Image.getById(id).getLink();
    }
}
