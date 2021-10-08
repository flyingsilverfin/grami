package cli;

import com.vaticle.typedb.client.api.connection.TypeDBClient;
import com.vaticle.typedb.client.api.connection.TypeDBSession;
import com.vaticle.typedb.client.api.connection.TypeDBTransaction;
import com.vaticle.typeql.lang.TypeQL;
import loader.TypeDBLoader;
import org.junit.Test;
import util.TypeDBUtil;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TypeDBLoaderCLITest {

    @Test
    public void migrateTest() {
        String config = "src/test/resources/1.0.0/phoneCalls/dc.json";
        String database = "typedb_loader_cli_test";
        String uri = "127.0.0.1:1729";
        String[] args = {
                "load",
                "-c", config,
                "-db", database,
                "-tdb", uri,
                "-cm"
        };

        LoadOptions options = LoadOptions.parse(args);
        TypeDBLoader loader = new TypeDBLoader(options);
        loader.load();

        assertEquals(options.dataConfigFilePath, config);
        assertEquals(options.databaseName, database);
        assertEquals(options.typedbURI, uri);
        assertEquals(options.cleanMigration, true);
        assertEquals(options.loadSchema, false);
    }

    @Test
    public void migrateTestContinue() {
        String config = "src/test/resources/1.0.0/phoneCalls/dc.json";
        String db = "typedb_loader_cli_test";
        String uri = "127.0.0.1:1729";
        String[] cleanLoadArgs = {
                "load",
                "-c", config,
                "-db", db,
                "-tdb", uri,
                "-cm"
        };

        LoadOptions cleanLoadOptions = LoadOptions.parse(cleanLoadArgs);
        assertEquals(cleanLoadOptions.dataConfigFilePath, config);
        assertEquals(cleanLoadOptions.databaseName, db);
        assertEquals(cleanLoadOptions.typedbURI, uri);
        assertEquals(cleanLoadOptions.cleanMigration, true);
        assertEquals(cleanLoadOptions.loadSchema, false);

        // run import once
        new TypeDBLoader(cleanLoadOptions).load();
        // delete all the data
        clearData(uri, db);

        String[] continueArgs = {
                "load",
                "-c", config,
                "-db", db,
                "-tdb", uri
        };
        LoadOptions continueLoadOptions = LoadOptions.parse(continueArgs);
        assertEquals(continueLoadOptions.dataConfigFilePath, config);
        assertEquals(continueLoadOptions.databaseName, db);
        assertEquals(continueLoadOptions.typedbURI, uri);
        assertEquals(continueLoadOptions.cleanMigration, false);
        assertEquals(continueLoadOptions.loadSchema, false);

        // load all data and schema again
        new TypeDBLoader(continueLoadOptions).load();

        String[] continueLoadSchema = {
                "load",
                "-c", config,
                "-db", db,
                "-tdb", uri,
                "--loadSchema"
        };
        LoadOptions continueLoadSchemaOptions = LoadOptions.parse(continueLoadSchema);
        assertEquals(continueLoadSchemaOptions.dataConfigFilePath, config);
        assertEquals(continueLoadSchemaOptions.databaseName, db);
        assertEquals(continueLoadSchemaOptions.typedbURI, uri);
        assertEquals(continueLoadSchemaOptions.cleanMigration, false);
        assertEquals(continueLoadSchemaOptions.loadSchema, true);

        // load all data and schema again
        new TypeDBLoader(continueLoadSchemaOptions).load();
    }

    private void clearData(String uri, String db) {
        System.out.println("Cleaning all previous loaded data in: " + db);
        TypeDBClient client = TypeDBUtil.getClient(uri);
        try (TypeDBSession session = TypeDBUtil.getDataSession(client, db)) {
            try (TypeDBTransaction txn = session.transaction(TypeDBTransaction.Type.WRITE)) {
                txn.query().delete(TypeQL.parseQuery("match $x isa thing; delete $x isa thing;").asDelete());
                txn.commit();
            }
        }
        client.close();
    }
}
