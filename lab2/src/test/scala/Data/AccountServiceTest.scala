import Data.{AccountImpl, AccountService}
import munit.ScalaCheckSuite

class AccountServiceTest extends ScalaCheckSuite {

  // Fresh service for each test
  def freshService(): AccountService = new AccountImpl

  test("account does not exist before being added"):
    assert(!freshService().isAccountExisting("jean"))

  test("addAccount creates the account"):
    val svc = freshService()
    svc.addAccount("alice", 30.0)
    assert(svc.isAccountExisting("alice"))

  test("addAccount does not overwrite existing account"):
    val svc = freshService()
    svc.addAccount("bob", 30.0)
    svc.addAccount("bob", 99.0)
    assert(svc.getAccountBalance("bob") == 30.0)

  test("getAccountBalance returns initial balance"):
    val svc = freshService()
    svc.addAccount("jean", 30.0)
    assert(svc.getAccountBalance("jean") == 30.0)

  test("getAccountBalance throws for unknown user"):
    val svc = freshService()
    intercept(svc.getAccountBalance("jean"))

  test("purchase deducts amount and returns new balance"):
    val svc = freshService()
    svc.addAccount("manu", 30.0)
    assert(svc.purchase("manu", 10.0).contains(20.0))
    assert(svc.getAccountBalance("manu") == 20.0)

  test("purchase returns None when balance is insufficient"):
    val svc = freshService()
    svc.addAccount("fred", 5.0)
    assert(svc.purchase("fred", 10.0).isEmpty)

  test("purchase does not modify balance on failure"):
    val svc = freshService()
    svc.addAccount("fred", 5.0)
    svc.purchase("fred", 10.0)
    assert(svc.getAccountBalance("fred") == 5.0)
}
