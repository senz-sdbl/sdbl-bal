package db

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.querybuilder.QueryBuilder._

case class Agent(username: String, branch: String)

case class Balance(agent: String, timestamp: String, account: String, nic: String, amount: String, status: String)

/**
 * Created by eranga on 1/19/16.
 */
class SenzDb(cluster: Cluster) {
  val session = cluster.connect("dev")

  def init() = {
    val sqlCreateTableAgent = "CREATE TABLE IF NOT EXISTS agent username TEXT PRIMARY KEY, branch TEXT;"

    val sqlCreateTableBalance = "CREATE TABLE IF NOT EXISTS balance agent_id TEXT, timestamp TEXT, name TEXT, account TEXT, nic TEXT, amount TEXT, status TEXT, PRIMARY KEY(agent_id, timestamp);"
    val sqlCreateIndexBalanceStatus = "CREATE INDEX balance_status on balance(status);"

    val sqlCreateTableTransaction = "CREATE TABLE IF NOT EXISTS transaction agent_id TEXT, timestamp TEXT, account TEXT, amount TEXT, status TEXT, PRIMARY KEY(agent_id, timestamp);"
    val sqlCreateIndexTransactionStatus = "CREATE INDEX transaction_status on transaction(status);"
  }

  def addAgent(agent: Agent): Unit = {
    session.execute(s"INSERT INTO agent (username, branch) VALUES ('${agent.username}', '${agent.branch}');")
  }

  def getAgent(username: String): Agent = {
    val selectStmt = select().all()
      .from("agent")
      .where(QueryBuilder.eq("username", username))
      .limit(1)

    val resultSet = session.execute(selectStmt)
    val row = resultSet.one()

    Agent(row.getString("username"), row.getString("brancg"))
  }

  def addBalanceQuery(balance: Balance) = {
    session.execute(s"INSERT INTO balance_query (agent, timestamp, name, account, nic, amount, status) VALUES (${balance.amount}, '${balance.agent}');")
  }

  def getBalanceQuery() = {
    val selectStmt = select().all()
      .from("balance")
      .where(QueryBuilder.eq("agent_id", "1")).and(QueryBuilder.eq("timestamp", "w234234"))
      .limit(1)

    val resultSet = session.execute(selectStmt)
    val row = resultSet.one()

    Balance(row.getString("agent_id"), row.getString("timestamp"), row.getString("account"), row.getString("nic"), row.getString("amount"), row.getString("status"))
  }

  def updateBalanceQuery() = {

  }
}