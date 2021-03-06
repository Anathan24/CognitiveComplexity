import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.separator.RecordSeparatorPolicy;
import org.springframework.batch.item.xml.StaxWriterCallback;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class SpringBatch {
    private Bean container;
    private Treese processed;
    private Treese recovered;
    private Treese jmsTemplate;
    private FlatFileItemReader<Object> reader;
    private ExecutionContext executionContext;
    private ItmWriter writer;

    // org.springframework.batch.container.jms.BatchMessageListenerContainerIntegrationTests.testFailureAndRecovery()
//    @Test // removed to allow compilation
    public void testFailureAndRecovery() throws Exception {
        final RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(new NeverRetryPolicy());
        container.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(final Message msg) {
                try {
                    RetryCallback<Message, Exception> callback = new RetryCallback<Message, Exception>() {
                        @Override
                        public Message doWithRetry(RetryContext context) throws Exception {
                            try {
                                processed.add(((TextMessage) msg).getText());
                            }
                            catch (JMSException e) {
                                throw new IllegalStateException(e);
                            }
                            throw new RuntimeException("planned failure: " + msg);
                        }
                    };
                    RecoveryCallback<Message> recoveryCallback = new RecoveryCallback<Message>() {
                        @Override
                        public Message recover(RetryContext context) {
                            try {
                                recovered.add(((TextMessage) msg).getText());
                            }
                            catch (JMSException e) {
                                throw new IllegalStateException(e);
                            }
                            return msg;
                        }
                    };
                    retryTemplate.execute(callback, recoveryCallback, new DefaultRetryState(msg.getJMSMessageID()));
                }
                catch (Exception e) {
                    throw (RuntimeException) e;
                }
            }
        });
        container.initializeProxy();
        container.start();
        jmsTemplate.convertAndSend("queue", "foo");
        assertEquals("foo", processed.poll(5, TimeUnit.SECONDS));
        assertEquals("foo", recovered.poll(5, TimeUnit.SECONDS));
    }

    // org.springframework.batch.core.jsr.configuration.xml.JsrBeanDefinitionDocumentReaderTests.testArtifactUniqueness()
//    @Test // removed to allow compilation
    public void testArtifactUniqueness() throws Exception {
        JobExecution jobExecution = runJob("jsrUniqueInstanceTests", new Properties(), 10000L);
        String exitStatus = jobExecution.getExitStatus();

        assertTrue("Exit status must contain listener3", exitStatus.contains("listener3"));
        exitStatus = exitStatus.replace("listener3", "");

        assertTrue("Exit status must contain listener2", exitStatus.contains("listener2"));
        exitStatus = exitStatus.replace("listener2", "");

        assertTrue("Exit status must contain listener1", exitStatus.contains("listener1"));
        exitStatus = exitStatus.replace("listener1", "");

        assertTrue("Exit status must contain listener0", exitStatus.contains("listener0"));
        exitStatus = exitStatus.replace("listener0", "");

        assertTrue("Exit status must contain listener7", exitStatus.contains("listener7"));
        exitStatus = exitStatus.replace("listener7", "");

        assertTrue("Exit status must contain listener6", exitStatus.contains("listener6"));
        exitStatus = exitStatus.replace("listener6", "");

        assertTrue("Exit status must contain listener5", exitStatus.contains("listener5"));
        exitStatus = exitStatus.replace("listener5", "");

        assertTrue("Exit status must contain listener4", exitStatus.contains("listener4"));
        exitStatus = exitStatus.replace("listener4", "");

        assertTrue("exitStatus must be empty", "".equals(exitStatus));
    }

    // org.springframework.batch.item.database.ExtendedConnectionDataSourceProxyTests.testOperationWithDirectCloseCall()
//    @Test // removed to allow compilation
    public void testOperationWithDirectCloseCall() throws SQLException {
        Connection con = mock(Connection.class);
        DataSource ds = mock(DataSource.class);

        when(ds.getConnection()).thenReturn(con); // con1
        con.close();
        when(ds.getConnection()).thenReturn(con); // con2
        con.close();


        final ExtendedConnectionDataSourceProxy csds = new ExtendedConnectionDataSourceProxy(ds);

        Connection con1 = csds.getConnection();
        csds.startCloseSuppression(con1);
        Connection con1_1 = csds.getConnection();
        assertSame("should be same connection", con1_1, con1);
        con1_1.close(); // no mock call for this - should be suppressed
        Connection con1_2 = csds.getConnection();
        assertSame("should be same connection", con1_2, con1);
        Connection con2 = csds.getConnection();
        assertNotSame("shouldn't be same connection", con2, con1);
        csds.stopCloseSuppression(con1);
        assertTrue("should be able to close connection", csds.shouldClose(con1));
        con1_1 = null;
        con1_2 = null;
        con1.close();
        assertTrue("should be able to close connection", csds.shouldClose(con2));
        con2.close();


    }

    private <T> T mock(Class<T> connectionClass) {
        return null;
    }

    // org.springframework.batch.item.file.FlatFileItemReaderTests.testCustomRecordSeparatorMultilineBlankLineAfterEnd()
//    @Test // removed to allow compilation
    public void testCustomRecordSeparatorMultilineBlankLineAfterEnd() throws Exception {

        reader.setRecordSeparatorPolicy(new RecordSeparatorPolicy() {

            // 1 record = 2 lines
            boolean pair = true;

            @Override
            public boolean isEndOfRecord(String line) {
                if (StringUtils.hasText(line)) {
                    pair = !pair;
                }
                return pair;
            }

            @Override
            public String postProcess(String record) {
                return StringUtils.hasText(record) ? record : null;
            }

            @Override
            public String preProcess(String record) {
                return record;
            }
        });

        reader.setResource(getInputResource("testLine1\ntestLine2\n\n"));
        reader.open(executionContext);

        assertEquals("testLine1testLine2", reader.read());
        assertEquals(null, reader.read());

    }

    // org.springframework.batch.item.xml.StaxEventItemWriterTests.initWriterForSimpleCallbackTests()
    private void initWriterForSimpleCallbackTests() throws Exception {
        writer = createItemWriter();
        writer.setHeaderCallback(new StaxWriterCallback() {

            @Override
            public void write(XMLEventWriter writer) throws IOException {
                XMLEventFactory factory = XMLEventFactory.newInstance();
                try {
                    writer.add(factory.createStartElement("ns", "https://www.springframework.org/test", "group"));
                }
                catch (XMLStreamException e) {
                    throw new RuntimeException(e);
                }
            }

        });
        writer.setFooterCallback(new StaxWriterCallback() {

            @Override
            public void write(XMLEventWriter writer) throws IOException {
                XMLEventFactory factory = XMLEventFactory.newInstance();
                try {
                    writer.add(factory.createEndElement("ns", "https://www.springframework.org/test", "group"));
                }
                catch (XMLStreamException e) {
                    throw new RuntimeException(e);
                }

            }

        });
        writer.setRootTagName("{https://www.springframework.org/test}ns:testroot");
        writer.afterPropertiesSet();
    }

    private ItmWriter createItemWriter() {
        return null;
    }

    private Resource getInputResource(String s) {
        return null;
    }

    private void assertNotSame(String s, Connection con2, Connection con1) {

    }

    private void assertSame(String should_be_same_connection, Connection con1_1, Connection con1) {

    }

    private Returner when(Connection connection) {
        return null;
    }

    private void assertEquals(String foo, Object poll) {

    }

    private void assertTrue(String exit_status_must_contain_listener3, boolean listener3) {

    }

    private JobExecution runJob(String jsrUniqueInstanceTests, Properties properties, long l) {
        return null;
    }

    private class JobExecution {
        public String getExitStatus() {
            return null;
        }
    }

    private class Properties {
    }

    private class RetryTemplate {
        public void setRetryPolicy(NeverRetryPolicy neverRetryPolicy) {

        }

        public void execute(RetryCallback<Message, Exception> callback, RecoveryCallback<Message> recoveryCallback, DefaultRetryState defaultRetryState) {

        }
    }

    private class NeverRetryPolicy {
    }

    private class Bean {
        public void initializeProxy() {

        }

        public void start() {

        }

        public void setMessageListener(MessageListener messageListener) {

        }
    }

    private class Message {
        public Object getText() {
            return null;
        }

        public Object getJMSMessageID() {
            return null;
        }
    }

    private abstract class RetryCallback<T, T1> {
        public abstract Message doWithRetry(RetryContext context) throws Exception;
    }

    private class RetryContext {
    }

    private class TextMessage extends Message {
    }

    private class Treese {
        public void add(Object text) {

        }

        public Object poll(int i, TimeUnit seconds) {
            return null;
        }

        public void convertAndSend(String queue, String foo) {

        }
    }

    private class JMSException extends IllegalStateException {
    }

    private abstract class RecoveryCallback<T> {
        public abstract Message recover(RetryContext context);
    }

    private class DefaultRetryState {
        public DefaultRetryState(Object jmsMessageID) {
        }
    }

    private abstract class MessageListener {
        public abstract void onMessage(Message msg);
    }

    private class Returner {
        public void thenReturn(Connection con) {

        }
    }

    private class ExtendedConnectionDataSourceProxy {
        public ExtendedConnectionDataSourceProxy(DataSource ds) {
        }

        public Connection getConnection() {
            return null;
        }

        public void startCloseSuppression(Connection con1) {

        }

        public void stopCloseSuppression(Connection con1) {

        }

        public boolean shouldClose(Connection con1) {
            return false;
        }
    }

    private class ItmWriter {
        public void setHeaderCallback(StaxWriterCallback staxWriterCallback) {

        }

        public void setFooterCallback(StaxWriterCallback staxWriterCallback) {

        }

        public void setRootTagName(String s) {

        }

        public void afterPropertiesSet() {

        }
    }
}
