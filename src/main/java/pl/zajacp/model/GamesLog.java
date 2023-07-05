package pl.zajacp.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.zajacp.model.GameRecord;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
@DynamoDbBean
public class GamesLog {
    private List<GameRecord> gamesLog;
}