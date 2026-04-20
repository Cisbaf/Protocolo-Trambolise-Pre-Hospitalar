import { ButtonGroup, IconButton, Pagination } from "@chakra-ui/react";
import { LuChevronLeft, LuChevronRight } from "react-icons/lu";


interface PaginationDocsProps {
  count?: number;
  page?: number;
  pageSize?: number;
  onPageChange?: (page: number) => void;
}


export function PaginationCP(props: PaginationDocsProps) {

  const count = props.count || 0;
  const currentPage = props.page || 1;
  const pageSize = props.pageSize || 5; // 👈 igual ao backend

  return (
    <Pagination.Root
      count={count}
      pageSize={pageSize}
      page={currentPage} // 🔥 controlado
      onPageChange={(e) => props.onPageChange && props.onPageChange(e.page)} // 🔥 integra com filtro
    >
      <ButtonGroup variant="ghost" size="sm">
        
        <Pagination.PrevTrigger asChild>
          <IconButton disabled={currentPage === 1}>
            <LuChevronLeft />
          </IconButton>
        </Pagination.PrevTrigger>

        <Pagination.Items
          render={(page) => (
            <IconButton
              variant={page.value === currentPage ? "outline" : "ghost"}
            >
              {page.value}
            </IconButton>
          )}
        />

        <Pagination.NextTrigger asChild>
          <IconButton
            disabled={currentPage * pageSize >= count}
          >
            <LuChevronRight />
          </IconButton>
        </Pagination.NextTrigger>

      </ButtonGroup>
    </Pagination.Root>
  );
}