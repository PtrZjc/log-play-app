package pl.zajacp.rest.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GetGameRecordRequest {
    private Long timestamp;
    private String gameName;
}
