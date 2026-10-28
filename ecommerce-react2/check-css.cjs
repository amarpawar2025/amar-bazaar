const fs = require("fs");
const postcss = require("postcss");
const cssnano = require("cssnano");

async function main() {
  const files = [
    "src/index.css",
    "src/App.css",
    "src/customer/pages/Product/ProductCard.css",
    "src/customer/pages/Home/ShopByCategory/ShopeByCategory.css"
  ];

  for (const file of files) {
    const css = fs.readFileSync(file, "utf8");
    try {
      await postcss([cssnano]).process(css, { from: file });
      console.log("MINIFY OK:", file);
    } catch (e) {
      console.log("\nMINIFY ERROR:", file);
      console.log("Line:", e.line, "Column:", e.column);
      console.log(e.message);
    }
  }
}

main();
