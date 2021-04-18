import java.util.Arrays;
import java.util.List;

public class K9 {
    private static DualHashMap<String, Store> sStores;
    private final ProvLock mProviderLocks = new ProvLock();
    private final ProvLock mProviders = new ProvLock();

    // com.fsck.k9.mail.store.imap.ImapResponseParserTest.testFetchResponse()
//    @Test // Removed to allow compilation
    public void testFetchResponse() throws Exception {
        ImapResponseParser parser = createParser("* 1 FETCH (" +
                "UID 23 " +
                "INTERNALDATE \"01-Jul-2015 12:34:56 +0200\" " +
                "RFC822.SIZE 3456 " +
                "BODY[HEADER.FIELDS (date subject from)] \"<headers>\" " +
                "FLAGS (\\Seen))\r\n");

        ImapResponse response = parser.readResponse();

        assertEquals(3, response.size());
        assertEquals("1", response.getString(0));
        assertEquals("FETCH", response.getString(1));
        assertEquals("UID", response.getList(2).getString(0));
        assertEquals(23, response.getList(2).getNumber(1));
        assertEquals("INTERNALDATE", response.getList(2).getString(2));
        assertEquals("01-Jul-2015 12:34:56 +0200", response.getList(2).getString(3));
        assertEquals("RFC822.SIZE", response.getList(2).getString(4));
        assertEquals(3456, response.getList(2).getNumber(5));
        assertEquals("BODY", response.getList(2).getString(6));
        assertEquals(2, response.getList(2).getList(7).size());
        assertEquals("HEADER.FIELDS", response.getList(2).getList(7).getString(0));
        assertEquals(3, response.getList(2).getList(7).getList(1).size());
        assertEquals("date", response.getList(2).getList(7).getList(1).getString(0));
        assertEquals("subject", response.getList(2).getList(7).getList(1).getString(1));
        assertEquals("from", response.getList(2).getList(7).getList(1).getString(2));
        assertEquals("<headers>", response.getList(2).getString(8));
        assertEquals("FLAGS", response.getList(2).getString(9));
        assertEquals(1, response.getList(2).getList(10).size());
        assertEquals("\\Seen", response.getList(2).getList(10).getString(0));
    }

    // com.fsck.k9.mail.store.RemoteStore.getInstance(android.content.Context,com.fsck.k9.mail.store.StoreConfig)
    /**
     * Get an instance of a remote mail store.
     */
    public static synchronized Store getInstance(Context context, StoreConfig storeConfig) throws MessagingException {
        String uri = storeConfig.getStoreUri();

        if (uri.startsWith("local")) {
            throw new RuntimeException("Asked to get non-local Store object but given LocalStore URI");
        }

        Store store = sStores.get(uri);
        if (store == null) {
            if (uri.startsWith("imap")) {
                OAuth2TokenProvider oAuth2TokenProvider = null;
                store = new ImapStore(
                        storeConfig,
                        new DefaultTrustedSocketFactory(context),
                        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE),
                        oAuth2TokenProvider);
            } else if (uri.startsWith("pop3")) {
                store = new Pop3Store(storeConfig, new DefaultTrustedSocketFactory(context));
            } else if (uri.startsWith("webdav")) {
                store = new WebDavStore(storeConfig, new WebDavHttpClientFactory());
            }

            if (store != null) {
                sStores.put(uri, store);
            }
        }

        if (store == null) {
            throw new MessagingException("Unable to locate an applicable Store for " + uri);
        }

        return store;
    }

    // com.fsck.k9.mail.transport.SmtpTransportTest.open_withXoauth2Extension_shouldThrowOnMultipleFailure()
//    @Test // Removed to allow compilation
    public void open_withXoauth2Extension_shouldThrowOnMultipleFailure() throws Exception {
        MockSmtpServer server = new MockSmtpServer();
        server.output("220 localhost Simple Mail Transfer Service Ready");
        server.expect("EHLO localhost");
        server.output("250-localhost Hello client.localhost");
        server.output("250 AUTH XOAUTH2");
        server.expect("AUTH XOAUTH2 dXNlcj11c2VyAWF1dGg9QmVhcmVyIG9sZFRva2VuAQE=");
        server.output("334 " + XOAuth2ChallengeParserTest.STATUS_400_RESPONSE);
        server.expect("");
        server.output("535-5.7.1 Username and Password not accepted. Learn more at");
        server.output("535 5.7.1 http://support.google.com/mail/bin/answer.py?answer=14257 hx9sm5317360pbc.68");
        server.expect("AUTH XOAUTH2 dXNlcj11c2VyAWF1dGg9QmVhcmVyIG5ld1Rva2VuAQE=");
        server.output("334 " + XOAuth2ChallengeParserTest.STATUS_400_RESPONSE);
        server.expect("");
        server.output("535-5.7.1 Username and Password not accepted. Learn more at");
        server.output("535 5.7.1 http://support.google.com/mail/bin/answer.py?answer=14257 hx9sm5317360pbc.68");
        SmtpTransport transport = startServerAndCreateSmtpTransport(server, AuthType.XOAUTH2, ConnectionSecurity.NONE);

        try {
            transport.open();
            fail("Exception expected");
        } catch (AuthenticationFailedException e) {
            assertEquals(
                    "Negative SMTP reply: 535 5.7.1 http://support.google.com/mail/bin/answer.py?answer=14257 hx9sm5317360pbc.68",
                    e.getMessage());
        }

        server.verifyConnectionStillOpen();
        server.verifyInteractionCompleted();
    }

    private static class DualHashMap<T, T1> {
        public T1 get(T uri) {
            return null;
        }

        public void put(T uri, T1 store) {

        }
    }

    public class StorageManager {
        private final Context context;
        // com.fsck.k9.mailstore.StorageManager.StorageManager(android.content.Context)

        /**
         * @param context Never <code>null</code>.
         * @throws NullPointerException If <tt>context</tt> is <code>null</code>.
         */
        protected StorageManager(final Context context) throws NullPointerException {
            if (context == null) {
                throw new NullPointerException("No Context given");
            }

            this.context = context;

            /*
             * 20101113/fiouzy:
             *
             * Here is where we define which providers are used, currently we only
             * allow the internal storage and the regular external storage.
             *
             * HTC Incredible storage and Samsung Galaxy S are omitted on purpose
             * (they're experimental and I don't have those devices to test).
             *
             *
             * !!! Make sure InternalStorageProvider is the first provider as it'll
             * be considered as the default provider !!!
             */
            final List<StorageProvider> allProviders = Arrays.asList(new InternalStorageProvider(),
                    new ExternalStorageProvider());
            for (final StorageProvider provider : allProviders) {
                // check for provider compatibility
                if (provider.isSupported(context)) {
                    // provider is compatible! proceeding

                    provider.init(context);
                    mProviders.put(provider.getId(), provider);
                    mProviderLocks.put(provider, new SynchronizationAid());
                }
            }

        }
    }

    // com.fsck.k9.message.html.HtmlConverterTest.testTextQuoteToHtmlBlockquote()
//    @Test // Removed to allow compilation
    public void testTextQuoteToHtmlBlockquote() {
        String message = "Panama!\r\n" +
                "\r\n" +
                "Bob Barker <bob@aol.com> wrote:\r\n" +
                "> a canal\r\n" +
                ">\r\n" +
                "> Dorothy Jo Gideon <dorothy@aol.com> espoused:\r\n" +
                "> >A man, a plan...\r\n" +
                "> Too easy!\r\n" +
                "\r\n" +
                "Nice job :)\r\n" +
                ">> Guess!";
        String result = HtmlConverter.textToHtml(message);
        writeToFile(result);
        assertEquals("<pre class=\"k9mail\">"
                + "Panama!<br />"
                + "<br />"
                + "Bob Barker &lt;bob@aol.com&gt; wrote:<br />"
                +
                "<blockquote class=\"gmail_quote\" style=\"margin: 0pt 0pt 1ex 0.8ex; border-left: 1px solid #729fcf; padding-left: 1ex;\">"
                + " a canal<br />"
                + "<br />"
                + " Dorothy Jo Gideon &lt;dorothy@aol.com&gt; espoused:<br />"
                +
                "<blockquote class=\"gmail_quote\" style=\"margin: 0pt 0pt 1ex 0.8ex; border-left: 1px solid #ad7fa8; padding-left: 1ex;\">"
                + "A man, a plan...<br />"
                + "</blockquote>"
                + " Too easy!<br />"
                + "</blockquote>"
                + "<br />"
                + "Nice job :)<br />"
                +
                "<blockquote class=\"gmail_quote\" style=\"margin: 0pt 0pt 1ex 0.8ex; border-left: 1px solid #729fcf; padding-left: 1ex;\">"
                +
                "<blockquote class=\"gmail_quote\" style=\"margin: 0pt 0pt 1ex 0.8ex; border-left: 1px solid #ad7fa8; padding-left: 1ex;\">"
                + " Guess!"
                + "</blockquote>"
                + "</blockquote>"
                + "</pre>", result);
    }

    private void writeToFile(String result) {

    }

    private SmtpTransport startServerAndCreateSmtpTransport(MockSmtpServer server, Object xoauth2, Object none) {
        return null;
    }

    private static class Context {
        public static final Object CONNECTIVITY_SERVICE = null;

        public Object getSystemService(Object connectivityService) {
            return null;
        }
    }

    private class SynchronizationAid {
    }

    private class ProvLock {
        public void put(Object provider, StorageProvider synchronizationAid) {

        }

        public void put(StorageProvider provider, SynchronizationAid synchronizationAid) {

        }
    }

    public interface StorageProvider {
        String getId();

        boolean isSupported(Context context);

        void init(Context context);
    }

    public static class InternalStorageProvider implements StorageProvider {

        @Override
        public String getId() {
            return null;
        }

        @Override
        public boolean isSupported(Context context) {
            return false;
        }

        @Override
        public void init(Context context) {

        }
    }

    public static class ExternalStorageProvider implements StorageProvider {

        @Override
        public String getId() {
            return null;
        }

        @Override
        public boolean isSupported(Context context) {
            return false;
        }

        @Override
        public void init(Context context) {

        }
    }

    private class ImapResponseParser {
        public ImapResponse readResponse() {
            return null;
        }
    }

    private class ImapResponse {
        public int size() {
            return 0;
        }

        public String getString(int i) {
            return null;
        }

        public ImapResponse getList(int i) {
            return null;
        }

        public int getNumber(int i) {
            return 0;
        }
    }

    private ImapResponseParser createParser(String s) {
        return null;
    }



    private static class Store {
    }

    private static class StoreConfig {
        public String getStoreUri() {
            return null;
        }
    }

    private static class MessagingException extends Exception {
        public MessagingException(String s) {

        }
    }

    private static class TValue {
    }

    private static class OAuth2TokenProvider {
    }

    private static class ConnectivityManager {
    }

    private static class DefaultTrustedSocketFactory {
        public DefaultTrustedSocketFactory(Context context) {
        }
    }

    private static class ImapStore extends Store {
        public ImapStore(StoreConfig storeConfig, DefaultTrustedSocketFactory defaultTrustedSocketFactory, ConnectivityManager systemService, OAuth2TokenProvider oAuth2TokenProvider) {
            super();
        }
    }

    private static class Pop3Store extends Store {
        public Pop3Store(StoreConfig storeConfig, DefaultTrustedSocketFactory defaultTrustedSocketFactory) {
            super();
        }
    }

    private static class WebDavHttpClientFactory {
    }

    private static class WebDavStore extends Store {
        public WebDavStore(StoreConfig storeConfig, WebDavHttpClientFactory webDavHttpClientFactory) {
            super();
        }
    }

    private class MockSmtpServer {
        public void output(String s) {
        }

        public void expect(String ehlo_localhost) {

        }

        public void verifyConnectionStillOpen() {

        }

        public void verifyInteractionCompleted() {

        }
    }

    private static class XOAuth2ChallengeParserTest {
        public static final String STATUS_400_RESPONSE = "";
    }

    private static class ConnectionSecurity {
        public static final Object NONE = null;
    }

    private static class AuthType {
        public static final Object XOAUTH2 = null;
    }

    private class SmtpTransport {
        public void open() throws AuthenticationFailedException{

        }
    }

    private class AuthenticationFailedException extends Exception{
        public String getMessage() {
            return null;
        }
    }

    private static class HtmlConverter {
        public static String textToHtml(String message) {
            return null;
        }
    }

    private void assertEquals(int i, int number) {

    }

    private void assertEquals(String s, String string) {

    }

    private void fail(String exception_expected) {

    }
}
