package org.sculptor.betting.customer.serviceapi;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sculptor.betting.customer.domain.CustomerBet;
import org.sculptor.betting.customer.mapper.CustomerBetMapper;
import org.sculptor.framework.accessimpl.mongodb.DbManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Spring based test with MongoDB.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-test.xml" })
public class BettingQueryServiceTest extends AbstractJUnit4SpringContextTests implements BettingQueryServiceTestBase {

	@Autowired
	private DbManager dbManager;

	@Autowired
	private BettingQueryService bettingQueryService;

	@Before
	public void initTestData() {
	}

	@Before
	public void initDbManagerThreadInstance() throws Exception {
		// to be able to do lazy loading of associations inside test class
		DbManager.setThreadInstance(dbManager);
	}

	@After
	public void dropDatabase() {
		Set<String> names = dbManager.getDB().getCollectionNames();
		for (String each : names) {
			if (!each.startsWith("system")) {
				dbManager.getDB().getCollection(each).drop();
			}
		}
		// dbManager.getDB().dropDatabase();
	}

	private int countRowsInDBCollection(String name) {
		return (int) dbManager.getDBCollection(name).getCount();
	}

	@Test
	public void testGetHighStakes() throws Exception {
		waitForConsume();
		List<CustomerBet> highStakes = bettingQueryService.getHighStakes(2.0);
		assertFalse(highStakes.isEmpty());
	}

	private void waitForConsume() throws InterruptedException {
		for (int i = 0; i < 5; i++) {
			System.out.println("# waiting for message");
			Thread.sleep(1000);
			if (countRowsInDBCollection(CustomerBetMapper.getInstance().getDBCollectionName()) > 0) {
				return;
			}
		}
		fail("No rows in CustomerBet");
	}

}
