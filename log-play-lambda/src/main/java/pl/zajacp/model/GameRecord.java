package pl.zajacp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
public class GameRecord {
    @JsonIgnore
    private String userName;
    private Long timestamp;
    private String gameName;
    private String gameDate;
    private String gameDescription;
    private boolean solo;
    private String duration;
    private List<PlayerResult> playerResults;

    @DynamoDbPartitionKey
    public String getGameName() {
        return gameName;
    }

    @DynamoDbSortKey
    public Long getTimestamp() {
        return timestamp;
    }
}

