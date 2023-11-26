'use client'

import {useEffect} from "react";
import {getGamesLog} from "@/app/apiClient";

export default function Home() {

    useEffect(() => {
        getGamesLog();
    }, []);

    return (
        <main className="pt-24">
            HI There!
        </main>
    )
}
