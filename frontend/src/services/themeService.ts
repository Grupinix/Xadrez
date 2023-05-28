export default {
  getThemeList(): {[key: string]: string;}[] {
    return [
      {
        value: "default",
        label: "Padrão",
      },
      {
        value: "chad",
        label: "Chad",
      },
      {
        value: "arroz",
        label: "Arroz",
      },
      {
        value: "pink",
        label: "Pink",
      },
    ];
  },

  getThemeName(): string {
    return localStorage.getItem("themeName") || "default";
  },

  setThemeName(themeName: string): void {
    localStorage.setItem("themeName", themeName);
  }
};
