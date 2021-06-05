// SPDX-License-Identifier: MIT

pragma solidity ^0.6.0;

import "@openzeppelin/contracts/introspection/IERC1820Registry.sol";
import "@openzeppelin/contracts/token/ERC777/IERC777Recipient.sol";
import "@openzeppelin/contracts/math/SafeMath.sol";

import "./AdminControl.sol";
import "./SponsorWhitelistControl.sol";
import "./Staking.sol";

interface ICRCN {
    function isOwner() external returns (bool);

    function create(
        address _initialOwner,
        uint256 _initialSupply,
        uint256 _cap,
        string memory _uri,
        bytes memory _data
    ) external returns (uint256);
}

contract FCExchange {
    using SafeMath for uint256;

    // 用户信息
    struct UserInfo {
        // 当前质押fc数量
        uint256 amount; // current deposited fc amount
        uint256 profitDebt; // withdrawn cfx amount
        // 累积质押fc数量
        uint256 accumulateAmount; // accumulative deposited fc amount
        // 是否获得nft
        bool nftGranted; // has granted NFT token or not
        // 获得的nft代币id
        uint256 grantedTokenId; // granted NFT token id
        uint256 accProfit; // accumulative profit
    }

    // 地址和用户信息的映射
    mapping(address => UserInfo) public userInfos;

    // staking
    // 质押
    uint256 public lastStakingAmount;       // 上一次质押数量
    uint256 public lastStakingBlockNumber;  // 上一次质押区块号
    // fc supply
    uint256 public fcSupply;                // fc供应
    // accumulative cfx for each fc
    uint256 public accCfxPerFc;             // 每个fc累积的cfx
    // initialized
    bool public initialized;                // 是否初始化

    address public fcAddr;
    ICRCN public nft;

    uint256 public constant fcCap = 21000000 * (10**18);
    uint256 public constant grantRequired = 10**21;

    IERC1820Registry private constant ERC1820_REGISTRY = IERC1820Registry(
        address(0x88887eD889e776bCBe2f0f9932EcFaBcDfCd1820)
    );

    // internal contracts
    // 内置合约：赞助白名单、质押、管理控制
    SponsorWhitelistControl public constant SPONSOR = SponsorWhitelistControl(
        address(0x0888000000000000000000000000000000000001)
    );

    Staking public constant STAKING = Staking(
        address(0x0888000000000000000000000000000000000002)
    );

    AdminControl public constant adminControl = AdminControl(
        address(0x0888000000000000000000000000000000000000)
    );

    // 质押事件
    event Deposit(address indexed user, uint256 indexed amount, uint256 profit);
    event Profit(
        uint256 indexed lastBlockNumber,
        uint256 indexed currentBlockNumber,
        uint256 indexed profit
    );
    event Withdraw(address indexed user, uint256 amount, uint256 profit);
    event InstantExchange(address indexed user, uint256 amount);
    // nft获得事件
    event NftGranted(address indexed userAddr, uint256 indexed tokenId);

    modifier onlyInitialized() {
        require(initialized, "FCExchange: contract is not initialized");
        _;
    }

    // 构造函数
    constructor(address _fcAddr, address _nftAddr) public {
        fcAddr = _fcAddr;
        nft = ICRCN(_nftAddr);
        fcSupply = 0;
        accCfxPerFc = 0;
        lastStakingAmount = 0;
        initialized = false;

        // register 1820
        ERC1820_REGISTRY.setInterfaceImplementer(
            address(this),
            keccak256("ERC777TokensRecipient"),
            address(this)
        );

        // register all users as sponsees
        // 注册所有的用户为被赞助者
        address[] memory users = new address[](1);
        users[0] = address(0);
        SPONSOR.addPrivilege(users);

        // remove contract admin
        // 移除合约管理员
        adminControl.setAdmin(address(this), address(0));
        require(
            adminControl.getAdmin(address(this)) == address(0),
            "require admin == null"
        );
    }

    // receive 2100w cfx and stake it to internal contract
    // 接收2100w个cfx并质押进内部合约
    function initialize() public payable {
        require(
            nft.isOwner(),
            "FCExchange: exchange contract is not owner of CRCN"
        );
        require(!initialized, "FCExchange: contract has been initialized");
        require(
            msg.value == fcCap,
            "FCExchange: initalize value is not 21 million"
        );
        initialized = true;
        _stake();
    }

    // take all staked cfx and profit from internal contract
    // 获取所有质押的cfx并从内部合约中取出收益
    function _withdrawAndUpdateProfit() internal {
        if (lastStakingAmount > 0) {
            // cfx remained in exchange contract
            uint256 oldBalance = address(this).balance;
            STAKING.withdraw(lastStakingAmount);
            // last staked amount + profit
            uint256 stakingAndProfit = address(this).balance.sub(oldBalance);
            // profit
            uint256 stakingProfit = stakingAndProfit.sub(lastStakingAmount);
            // update accumulative cfx per fc
            if (fcSupply > 0 && stakingProfit > 0) {
                accCfxPerFc = accCfxPerFc.add(
                    stakingProfit.mul(1e18).div(fcSupply)
                );
            }
            emit Profit(lastStakingBlockNumber, block.number, stakingProfit);
            lastStakingAmount = 0;
            lastStakingBlockNumber = block.number;
        }
    }

    // stake all cfx in internal contract
    // 在内部合约里质押所有的cfx
    function _stake() internal {
        if (address(this).balance > 0) {
            lastStakingAmount = address(this).balance;
            lastStakingBlockNumber = block.number;
            STAKING.deposit(address(this).balance);
        }
    }

    // grant nft to user if it's the first time his/her accumulative deposited FC amount is larger than 1000
    // 如果用户第一次累积存入的FC金额大于1000，则授予用户nft
    function _tryGrantNft(address userAddr, UserInfo storage user) internal {
        if (!user.nftGranted && user.accumulateAmount >= grantRequired) {
            // mint exact 1 Conflux Guardian token to user and get unique token ID
            user.grantedTokenId = nft.create(
                userAddr,
                1,
                1,
                "Conflux Guardian",
                ""
            );
            user.nftGranted = true;
            emit NftGranted(userAddr, user.grantedTokenId);
        }
    }

    // transfer pending profit to user
    // update user deposited FC amount
    function _deposit(address userAddr, uint256 amount) internal {
        UserInfo storage user = userInfos[userAddr];
        // take staking & profit from internal contract
        _withdrawAndUpdateProfit();
        // transfer pending profit to  user
        uint256 pendingProfit = 0;
        if (user.amount > 0) {
            pendingProfit = user.amount.mul(accCfxPerFc).div(1e18).sub(
                user.profitDebt
            );
            require(
                address(this).balance >= pendingProfit,
                "FCExchange deposit: cfx balance insufficient"
            );
            address(uint160(userAddr)).transfer(pendingProfit);
        }
        // update fc supply
        fcSupply = fcSupply.add(amount);
        // update user info
        user.amount = user.amount.add(amount);
        user.accumulateAmount = user.accumulateAmount.add(amount);
        user.profitDebt = user.amount.mul(accCfxPerFc).div(1e18);
        user.accProfit = user.accProfit.add(pendingProfit);
        // try grant nft
        _tryGrantNft(userAddr, user);
        // stake remaining cfx to internal contract
        _stake();
        emit Deposit(userAddr, amount, pendingProfit);
    }

    // transfer pending profit and exchange amount of cfx to user
    // 将cfx的待定利润和兑换金额转移给用户
    function _instantExchange(address userAddr, uint256 amount) internal {
        UserInfo storage user = userInfos[userAddr];
        // take staking & profit from internal contract
        _withdrawAndUpdateProfit();
        // transfer withdrawal cfx to user
        require(
            address(this).balance >= amount,
            "FCExchange instant exchange: cfx balance insufficient"
        );
        address(uint160(userAddr)).transfer(amount);
        // update user info
        user.accumulateAmount = user.accumulateAmount.add(amount);
        // try grant nft
        _tryGrantNft(userAddr, user);
        // stake remaining cfx to internal contract
        _stake();
        emit InstantExchange(userAddr, amount);
    }

    // withdraw cfx and pending profit to user, reduce corresponding amount of FC deposited by user
    // this function can be used to check the pending profit of user
    // 向用户提取cfx和未结利润，减少用户存入的相应FC金额 此功能可用于查看用户的未结利润
    function withdraw(uint256 amount) public onlyInitialized returns (uint256) {
        UserInfo storage user = userInfos[msg.sender];
        require(user.amount > 0, "FCExchange: user FC deposit amount is zero"); // 用户FC存款金额为零
        require(
            user.amount >= amount,
            "FCExchange: user deposited FC is insufficient" // 用户存入FC不足
        );
        // take staking & profit from internal contract
        // 从内部合约中获取权益
        _withdrawAndUpdateProfit();
        // transfer pending profit and withdrawal cfx to user
        // 将未决利润和提款 cfx 转移给用户
        uint256 pendingProfit = user.amount.mul(accCfxPerFc).div(1e18).sub(
            user.profitDebt
        );
        require(
            address(this).balance >= pendingProfit.add(amount),
            "FCExchange withdraw: cfx balance insufficient"
        );
        msg.sender.transfer(pendingProfit.add(amount));
        // update fc supply
        fcSupply = fcSupply.sub(amount);
        // update user info
        // 更新用户信息
        user.amount = user.amount.sub(amount);
        user.profitDebt = user.amount.mul(accCfxPerFc).div(1e18);
        user.accProfit = user.accProfit.add(pendingProfit);
        // stake remaining cfx to internal contract
        // 将剩余的 cfx 权益存入内部合约
        _stake();
        emit Withdraw(msg.sender, amount, pendingProfit);
        return pendingProfit;
    }

    /* === for ERC1820 === */
    // if userData is empty, received FC will be exchanged to cfx and send to user instantly
    // otherwise, received FC will be deposited and share profit
    function tokensReceived(
        address,
        address from,
        address to,
        uint256 amount,
        bytes calldata userData,
        bytes calldata
    ) external onlyInitialized {
        require(to == address(this), "FCExchange: deposit not to FCExchange");
        require(msg.sender == fcAddr, "FCExchange: token received is not FC");
        require(amount > 0, "FCExchange: amount is zero");
        if (userData.length > 0) {
            _deposit(from, amount);
        } else {
            _instantExchange(from, amount);
        }
    }
}
