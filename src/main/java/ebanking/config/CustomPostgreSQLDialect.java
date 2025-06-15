package ebanking.config;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;

public class CustomPostgreSQLDialect extends PostgreSQLDialect {
    // 無參 constructor，向下相容
    public CustomPostgreSQLDialect() {
        super();
    }

    // Hibernate 6 啟動自動設定時會用到這一個
    public CustomPostgreSQLDialect(DialectResolutionInfo info) {
        super(info);
    }

    @Override
    public String getCreateIndexString(boolean unique) {
        return unique
            ? "create unique index if not exists"
            : "create index if not exists";
    }
}