package protocols

case class Agent(account: String, branch: String)

case class Bal(agent: String, customer: String, amount: Int, timestamp: String, status: String)

case class BalMsg(msgStream: Array[Byte])

case class BalResp(esh: String, status: String, rst: String)
