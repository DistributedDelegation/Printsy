package com.mvp.resolver;

import com.mvp.model.Image;
import org.springframework.stereotype.Component;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import graphql.schema.DataFetcher;
import org.springframework.stereotype.Controller;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Component
@Controller
public class Query {
    private static final Logger logger = LoggerFactory.getLogger(Query.class);

    @QueryMapping
    public Image randomImage() {
        return Image.getRandom();
    }

    @QueryMapping
    public Image getImageById(@Argument String id) {
        return Image.getById(id);
    }

}
