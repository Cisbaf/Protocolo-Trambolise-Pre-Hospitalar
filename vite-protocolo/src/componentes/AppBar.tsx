import {
  Flex,
  Text,
} from "@chakra-ui/react";
import { Link } from "react-router-dom";

export default function AppBar() {

    return (
        <Flex
            as="header"
            w="100%"
            px={6}
            py={4}
            align="center"
            justify="space-between"
            bg="white"
            borderBottom="1px solid"
            borderColor="gray.200"
            position="sticky"
            top="0"
            zIndex="1000"
            >
                {/* LOGO */}
                <Link to="/">
                    <Text fontWeight="bold" fontSize="lg">
                        CISBAF
                    </Text>
                </Link>
            </Flex>
    )
}