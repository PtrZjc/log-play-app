package pl.zajacp.rest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.zajacp.repository.ItemQueryKey;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetGameRecordRequest {
    private String gameName;
    private Long timestamp;

    public ItemQueryKey toItemQuery() {
        return ItemQueryKey.of(
                "gameName", getGameName(),
                getTimestamp() != null ? "timestamp" : null, getTimestamp()
        );
    }
}
