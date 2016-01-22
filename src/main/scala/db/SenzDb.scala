package db

import com.datastax.driver.core.Cluster

case class Agent(id: Int, username: String, branch: String)

case class BalanceQuery(id: Int, agent: String, nic: String, amount: String, status: String)

/**
 * Created by eranga on 1/19/16.
 */
class SenzDb(cluster: Cluster) {
  val session = cluster.connect("dev")

  def init() = {
    val sqlCreateTableAgent = "CREATE TABLE IF NOT EXISTS agent username TEXT PRIMARY KEY, branch TEXT;"

    val sqlCreateTableBalance = "CREATE TABLE IF NOT EXISTS balance agent_id TEXT, timestamp TEXT, name TEXT, nic TEXT, amount TEXT, status TEXT, PRIMARY KEY(agent_id, timestamp);"
    val sqlCreateIndexBalanceStatus = "CREATE INDEX balance_status on balance(status);"

    val sqlCreateTableTransaction = "CREATE TABLE IF NOT EXISTS transaction agent_id TEXT, timestamp TEXT, account TEXT, amount TEXT, status TEXT, PRIMARY KEY(agent_id, timestamp);"
    val sqlCreateIndexTransactionStatus = "CREATE INDEX transaction_status on transaction(status);"
  }

  def addUser(agent: Agent): Unit = {
    //session.executeAsync(preparedStatement.bind(id, name))
    session.execute(s"INSERT INTO agent (id, name, branch) VALUES (${agent.id}, '${agent.username}', '${agent.branch}');")
  }

  def getUser(id: Int) = {

  }

  def addBalanceQuery(balanceQuery: BalanceQuery) = {
    session.execute(s"INSERT INTO balance_query (id, agent, nic, name, amount, status) VALUES (${balanceQuery.id}, '${balanceQuery.agent}');")
  }

  def getBalanceQuery() = {

  }

  def updateBalanceQuery() = {

  }
}