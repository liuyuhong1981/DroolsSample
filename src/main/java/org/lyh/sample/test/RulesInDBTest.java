/**
 * 
 */
package org.lyh.sample.test;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * @author liuyuho
 *
 */
public class RulesInDBTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		testRulesInString();
	}

	public static void testRulesInDB() {

	}

	public static void testRulesInString() {

		// read and build the drl string
		StringBuffer sb = new StringBuffer();
		sb.append("package org.lyh.sample\n");
		sb.append("import org.lyh.sample.test.DroolsTest.Message;\n");

		sb.append("rule \"Hello World\"\n");
		sb.append("when\n");
		sb.append("m : Message( status == Message.HELLO, myMessage : message )\n");
		sb.append("then\n");
		sb.append("System.out.println( \"111\" );\n");
		//sb.append("m.setMessage( \"Goodbye cruel world\" );\n");
		//sb.append("m.setStatus( Message.GOODBYE );\n");
		//sb.append("update( m );\n");
		sb.append("end\n");

//		sb.append("rule \"GoodBye\"\n");
//		sb.append("when\n");
//		sb.append("Message( status == Message.GOODBYE, myMessage : message )\n");
//		sb.append("then\n");
//		sb.append("System.out.println( myMessage );\n");
//		sb.append("end\n");
		
		System.out.println(sb.toString());

		KieServices kieServices = KieServices.Factory.get();
		KieFileSystem kfs = kieServices.newKieFileSystem();
		kfs.write("src/main/resources/simple.drl", sb.toString());
		KieBuilder kieBuilder = kieServices.newKieBuilder(kfs).buildAll();
		Results results = kieBuilder.getResults();
		if (results.hasMessages(org.kie.api.builder.Message.Level.ERROR)) {
			System.out.println(results.getMessages());
			throw new IllegalStateException("### errors ###");
		}
		KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
//		KieBase kieBase = kieContainer.getKieBase();
		KieSession kieSession = kieContainer.newKieSession();

		try {
			Message m = new Message();
			m.setMessage("Hello World");
			m.setStatus(Message.HELLO);

			kieSession.insert(m);
			kieSession.fireAllRules();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public static class Message {

		public static final int HELLO = 0;
		public static final int GOODBYE = 1;

		private String message;

		private int status;

		public String getMessage() {
			return this.message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public int getStatus() {
			return this.status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

	}
}
