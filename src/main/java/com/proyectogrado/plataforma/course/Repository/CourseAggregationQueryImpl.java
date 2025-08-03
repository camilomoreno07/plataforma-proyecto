package com.proyectogrado.plataforma.course.Repository;

import org.springframework.stereotype.Repository;

import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
public class CourseAggregationQueryImpl implements CourseAggregationQuery
{
    private MongoTemplate mongoTemplate;

    public CourseAggregationQueryImpl(MongoTemplate mongoTemplate)
    {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Document findStudentWithCourses(String username)
    {
        MatchOperation match = match(Criteria.where("username").is(username).and("role").is("STUDENT"));

        LookupOperation lookup = LookupOperation.newLookup()
                .from("courses")
                .localField("username")
                .foreignField("studentIds")
                .as("userCourses");

        ProjectionOperation project = project()
                .andExpression("concat(firstname, ' ', lastname)").as("student")
                .and("userCourses.courseName").as("courses");

        TypedAggregation<Document> aggregation = Aggregation.newAggregation(
                Document.class,
                match, lookup, project
        );

        return mongoTemplate.aggregate(aggregation, "user", Document.class).getRawResults();
    }
}
