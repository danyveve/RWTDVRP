package marcu.rwtdvrpapi.api.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Data
@Builder
public class MyPair<L, R> {
    private L left;
    private R right;
}
