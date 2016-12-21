/**
 * 
 */
package org.lyh.sample.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.template.ObjectDataCompiler;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.lyh.sample.test.DroolsTest.Message;

/**
 * @author liuyuho
 *
 */
public class DynamicDroolsRuleTest {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		DynamicDroolsRuleTest test = new DynamicDroolsRuleTest();
		// test.DroolsTemplate1Test();
		test.DroolsTemplate2Test();
	}

	public void DroolsTemplate1Test() throws IOException {
		Collection<Message> ms = new ArrayList<Message>();
		// 1, build fact object
		Message m = new Message();
		m.setStatus(Message.HELLO);
		m.setMessage("Hello World");
		ms.add(m);

		// 2, compile drl
		ObjectDataCompiler converter = new ObjectDataCompiler();
		InputStream templateStream = this.getClass().getResourceAsStream("/template/template.drl");
		String drl = converter.compile(ms, templateStream);

		// 3, load up the knowledge base and add drl
		KieServices ks = KieServices.Factory.get();
		KieFileSystem kfs = ks.newKieFileSystem();
		kfs.write("src/main/resources/simple.drl", drl);
		KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
		Results results = kieBuilder.getResults();

		if (results.hasMessages(org.kie.api.builder.Message.Level.ERROR)) {
			System.out.println(results.getMessages());
			throw new IllegalStateException("### errors ###");
		}

		// 4, use new KieContainer instead of KieClasspathContainer
		// KieContainer kContainer = ks.getKieClasspathContainer();
		// KieSession kSession = kContainer.newKieSession("ksession-rules");

		KieContainer kieContainer = ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
		KieSession kSession = kieContainer.newKieSession();

		// insert test data and run rules
		kSession.insert(m);
		kSession.fireAllRules();
	}

	public void DroolsTemplate2Test() throws IOException {
		Collection<Map<String, Object>> ms = new ArrayList<Map<String, Object>>();
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("ruleId", "rule_1");
		m.put("factType", Message.class.getName());
		m.put("whenStatement", "(status == 0, myMessage : message)");
		m.put("thenStatement", "System.out.println(myMessage);");
		ms.add(m);

		// 2, compile drl
		ObjectDataCompiler converter = new ObjectDataCompiler();
		InputStream templateStream = this.getClass().getResourceAsStream("/template/template2.drl");
		String drl = converter.compile(ms, templateStream);
		System.out.println(drl);

		// 3, load up the knowledge base and add drl
		KieServices ks = KieServices.Factory.get();
		KieFileSystem kfs = ks.newKieFileSystem();
		kfs.write("src/main/resources/simple.drl", drl);
		KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
		Results results = kieBuilder.getResults();

		if (results.hasMessages(org.kie.api.builder.Message.Level.ERROR)) {
			System.out.println(results.getMessages());
			throw new IllegalStateException("### errors ###");
		}

		// 4, use new KieContainer instead of KieClasspathContainer
		// KieContainer kContainer = ks.getKieClasspathContainer();
		// KieSession kSession = kContainer.newKieSession("ksession-rules");

		KieContainer kieContainer = ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
		KieSession kSession = kieContainer.newKieSession();

		// insert test data and run rules
		Message mes = new Message();
		mes.setStatus(Message.HELLO);
		mes.setMessage("222");
		kSession.insert(mes);
		kSession.fireAllRules();
	}
}
