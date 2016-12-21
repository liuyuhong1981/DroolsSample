/**
 * 
 */
package org.lyh.sample.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

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
		test.DroolsTemplateTest();
	}

	public void DroolsTemplateTest() throws IOException {
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
//		KieContainer kContainer = ks.getKieClasspathContainer();
//		KieSession kSession = kContainer.newKieSession("ksession-rules");

		KieContainer kieContainer = ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
		KieSession kSession = kieContainer.newKieSession();

		// insert test data and run rules
		kSession.insert(m);
		kSession.fireAllRules();
	}
}
