export const metadata = {
  title: "LPA 2demo",
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
