package org.bidib.jbidibc.node;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bidib.jbidibc.AddressData;
import org.bidib.jbidibc.BidibInterface;
import org.bidib.jbidibc.MessageListener;
import org.bidib.jbidibc.MessageReceiver;
import org.bidib.jbidibc.Node;
import org.bidib.jbidibc.enumeration.BoosterState;
import org.bidib.jbidibc.enumeration.IdentifyState;
import org.bidib.jbidibc.exception.InvalidConfigurationException;
import org.bidib.jbidibc.node.listener.TransferListener;
import org.bidib.jbidibc.utils.ByteUtils;
import org.bidib.jbidibc.utils.NodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeFactory.class);

    private static final int ROOT_ADDRESS = 0;

    private final Map<Integer, BidibNode> nodes = Collections.synchronizedMap(new HashMap<Integer, BidibNode>());

    private MessageReceiver messageReceiver;

    private BidibInterface bidib;

    public NodeFactory() {
    }

    /**
     * @return the bidib
     */
    public BidibInterface getBidib() {
        return bidib;
    }

    /**
     * @param bidib the bidib to set
     */
    public void setBidib(BidibInterface bidib) {
        this.bidib = bidib;
    }

    /**
     * @param messageReceiver
     *            the message receiver to set
     */
    public void setMessageReceiver(MessageReceiver messageReceiver) {
        LOGGER.debug("Set the message receiver: {}", messageReceiver);
        this.messageReceiver = messageReceiver;

        messageReceiver.addMessageListener(new MessageListener() {
            @Override
            public void address(byte[] address, int detectorNumber, Collection<AddressData> addressData) {
            }

            @Override
            public void boosterCurrent(byte[] address, int current) {
            }

            @Override
            public void boosterState(byte[] address, BoosterState state) {
            }

            @Override
            public void boosterTemperature(byte[] address, int temperature) {
            }

            @Override
            public void boosterVoltage(byte[] address, int voltage) {
            }

            @Override
            public void confidence(byte[] address, int valid, int freeze, int signal) {
            }

            @Override
            public void free(byte[] address, int detectorNumber) {
            }

            @Override
            public void identity(byte[] address, IdentifyState identifyState) {
            }

            @Override
            public void key(byte[] address, int keyNumber, int keyState) {
            }

            @Override
            public void nodeLost(Node node) {
                LOGGER.debug("Node lost, node: {}", node);
                removeNode(node);
            }

            @Override
            public void nodeNew(Node node) {
                LOGGER.debug("Found new node in system: {}", node);

                // Q: why is removeNode called instead of newNode? 
                // A: to make sure that the node is created by the new "newNode-created-thread-" in MainController ...
                removeNode(node);
            }

            @Override
            public void occupied(byte[] address, int detectorNumber) {
            }

            @Override
            public void speed(byte[] address, AddressData addressData, int speed) {
            }

            @Override
            public void error(byte[] address, int errorCode) {
            }
        });
    }

    /**
     * Returns the provided node as AccessoryNode instance of null if the node is not an 
     * AccessoryNode.
     * @param node the node
     * @return the AccessoryNode instance
     */
    public AccessoryNode getAccessoryNode(Node node) {
        BidibNode result = getNode(node);

        if (result instanceof AccessoryNode) {
            return (AccessoryNode) result;
        }
        LOGGER.debug("The requested node is not an AccessoryNode.");
        return null;
    }

    /**
     * Returns the provided node as BoosterNode instance of null if the node is not an 
     * BoosterNode.
     * @param node the node
     * @return the BoosterNode instance
     */
    public BoosterNode getBoosterNode(Node node) {
        BidibNode bidibNode = getNode(node);

        if (NodeUtils.hasBoosterFunctions(node.getUniqueId())) {
            BoosterNode boosterNode = new BoosterNode(bidibNode);
            LOGGER.debug("prepared booster node: {}", boosterNode);
            return boosterNode;
        }

        LOGGER.debug("The requested node is not a BoosterNode.");
        throw new InvalidConfigurationException("The requested node is not a BoosterNode.");
    }

    /**
     * Returns the provided node as CommandStationNode instance of null if the node is not an 
     * CommandStationNode.
     * @param node the node
     * @return the CommandStationNode instance
     */
    public CommandStationNode getCommandStationNode(Node node) {
        BidibNode bidibNode = getNode(node);

        if (NodeUtils.hasCommandStationFunctions(node.getUniqueId())) {
            CommandStationNode commandStationNode = new CommandStationNode(bidibNode);
            LOGGER.debug("prepared command station node: {}", commandStationNode);
            return commandStationNode;
        }

        LOGGER.debug("The requested node is not a CommandStationNode.");
        throw new InvalidConfigurationException("The requested node is not a CommandStationNode.");
    }

    public BidibNode getNode(Node node) {
        LOGGER.debug("Get the bidibNode of node: {}", node);

        BidibNode bidibNode = null;
        synchronized (nodes) {
            int address = NodeUtils.convertAddress(node.getAddr());
            LOGGER.debug("Fetch bidibNode from nodes, address: {}", address);
            bidibNode = nodes.get(address);

            LOGGER.debug("Get the bidibNode from nodesSet with address: {}, bidibNode: {}", address, bidibNode);

            if (bidibNode == null) {
                // get the classId of the new node
                int classId = ByteUtils.convertLongToUniqueId(node.getUniqueId())[0];
                LOGGER.debug("Create new bidibNode with classId: {}", classId);

                // create the new node based on the class id
                if ((classId & 0x01) == 1) {
                    bidibNode = new AccessoryNode(node.getAddr(), messageReceiver);
                }
                else {
                    bidibNode = new BidibNode(node.getAddr(), messageReceiver);
                }
                // initialize the node
                bidibNode.setBidib(bidib);

                // verify that a transfer listener is available
                List<TransferListener> transferListeners = getRootNode().getTransferListeners();
                if (transferListeners != null && transferListeners.size() > 0) {
                    // add the transfer listener
                    bidibNode.addTransferListener(transferListeners.get(0));
                }
                else {
                    LOGGER.error("No transfer listener available on root node!!!!");
                    throw new RuntimeException("No transfer listener available on root node!!!!");
                }

                LOGGER.info("Create new bidibNode: {}, address: {}", bidibNode, address);

                nodes.put(address, bidibNode);
            }
        }
        return bidibNode;
    }

    /**
     * Get the root node of the system. This is the node that represents the master. Creates a new instance of root node
     * if no root node is stored.
     * 
     * @return the root node
     */
    public RootNode getRootNode() {
        LOGGER.debug("Get the root node.");
        RootNode rootNode = null;
        synchronized (nodes) {
            // get the node from the registered nodes
            rootNode = (RootNode) nodes.get(ROOT_ADDRESS);

            if (rootNode == null) {
                // no root node registered, create and initialize the root node.
                // the root node has always the local address 0 and is the interface node.
                LOGGER.debug("Create the root node.");
                rootNode = new RootNode(messageReceiver);
                // initialize the root node
                rootNode.setBidib(bidib);
                nodes.put(ROOT_ADDRESS, rootNode);
            }
            LOGGER.debug("Root node: {}", rootNode);
        }
        return rootNode;
    }

    private void removeNode(Node node) {
        synchronized (nodes) {
            LOGGER.debug("Remove node from bidib nodes: {}", node);
            BidibNode bidibNode = nodes.remove(NodeUtils.convertAddress(node.getAddr()));
            if (bidibNode == null) {
                LOGGER.warn("Remove bidibNode failed for address: {}", NodeUtils.convertAddress(node.getAddr()));
            }
        }
    }

    /**
     * Remove all stored nodes but keep the root node!
     */
    public void reset() {
        LOGGER.info("Reset the node factory.");
        synchronized (nodes) {
            LOGGER.debug("Remove all nodes but keep the root node.");
            RootNode rootNode = (RootNode) nodes.get(ROOT_ADDRESS);
            nodes.clear();
            if (rootNode != null) {
                // add the root node again
                nodes.put(ROOT_ADDRESS, rootNode);
            }
        }
    }
}
