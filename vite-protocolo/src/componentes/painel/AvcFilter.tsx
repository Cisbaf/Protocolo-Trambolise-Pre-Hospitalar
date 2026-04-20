import { useAvcManagerContext } from "../../context/AvcManagerContext";
import { DynamicFilterBuilder } from "./DynamicFilterBuilder";


export default function AvcFilter() {
    const { filter, form } = useAvcManagerContext();

    const resetConstruction = () => {
        form.setValue("number", 0);
    }

    return <DynamicFilterBuilder filter={filter} resetConstruction={resetConstruction}/>
}