package pl.zajacp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.zajacp.model.GameRecord;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.util.List;

@Data
@AllArgsConstructor
@DynamoDbBean
public class GamesLog {
    private List<GameRecord> gamesLog;
}