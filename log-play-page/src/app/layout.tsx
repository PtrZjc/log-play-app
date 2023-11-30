// import "./global.css";
import "./styles.module.css";
export const metadata = {
  title: "LPA form",
  description: "Ultimate boardgame sessions log",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
